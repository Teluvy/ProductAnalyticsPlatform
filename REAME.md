# Product Analytics Platform (Java + Kafka + ClickHouse)

## 1. Цель проекта

Разработать систему **реал-тайм аналитики пользовательских событий** с использованием **ClickHouse** как основного аналитического хранилища.

**Основная задача проекта** — глубоко продемонстрировать навыки работы с ClickHouse: правильное проектирование таблиц MergeTree, использование Materialized Views, Buffer Engine, TTL, оконных функций и эффективных аналитических запросов на больших объёмах данных.

Технологический стек: **Java (Spring Boot) + Apache Kafka + ClickHouse + Grafana**.

## 2. Архитектура

```
Event Generator → Kafka (user-events) → Spring Boot Service (Consumer + Batch Insert) → ClickHouse → Grafana
```

- **Event Generator** — отдельное приложение для генерации реалистичного потока событий.
- **Spring Boot Service** — единый сервис (Consumer + REST API).
- **ClickHouse** — основное хранилище и слой агрегаций.
- **Grafana** — дашборды с прямым подключением к ClickHouse.
- Всё запускается через `docker-compose`.

## 3. Компоненты системы

### 3.1 Event Generator
- Генерирует реалистичные пользовательские события с вероятностными воронками.
- Поддерживает настраиваемую нагрузку (events/sec и burst-режим).

### 3.2 Kafka
- Topic: `user-events` (3–6 партиций).
- Retention: 24–48 часов.

### 3.3 Spring Boot Service
- Kafka Consumer с `@KafkaListener`, ручным коммитом (`MANUAL_IMMEDIATE`).
- Накопление батча (по размеру или таймауту).
- Batch insert в ClickHouse.
- Обработка ошибок + Dead Letter Queue (`user-events-dlq`).
- REST API для ключевых аналитических метрик.
- Экспорт метрик через Micrometer + Actuator.

### 3.4 ClickHouse
- Основная таблица сырых событий.
- Буферная таблица (`Buffer` engine).
- Materialized Views для агрегаций.
- TTL для автоматического удаления старых данных.

### 3.5 Grafana
- Прямое подключение к ClickHouse.
- Realtime-дашборд с основными продуктовыми метриками.

## 4. Функциональные требования

### 4.1 Типы событий
`open_app`, `search`, `view_product`, `add_to_cart`, `remove_from_cart`, `buy_product`, `add_to_favorite`, `logout`.

### 4.2 Структура события
- `event_id` (UUID)
- `user_id` (UInt64)
- `session_id` (UUID)
- `event_type` (LowCardinality(String))
- `product_id` (UInt64, nullable)
- `category` (LowCardinality(String), nullable)
- `price` (Decimal(10,2))
- `device` (LowCardinality(String))
- `country` (FixedString(2))
- `created_at` (DateTime)

### 4.3 Обработка событий
- Consumer накапливает события в памяти.
- Выполняет batch insert (10 000 событий или каждые 5 секунд).
- Запись идёт в буферную таблицу `events_buffer`.

### 4.4 REST API (ключевые эндпоинты)

| Метод | Эндпоинт                        | Параметры               | Описание                          |
|------|---------------------------------|-------------------------|-----------------------------------|
| GET  | `/api/metrics/dau`             | `date`                  | DAU за выбранный день            |
| GET  | `/api/metrics/retention`       | `start_date`            | Day-1, Day-7 retention           |
| GET  | `/api/funnel`                  | `from`, `to`            | Конверсия воронки                |
| GET  | `/api/revenue`                 | `from`, `to`            | Выручка, средний чек, AOV        |
| GET  | `/api/top-products`            | `limit`, `period`       | Топ товаров по просмотрам/покупкам |

Все запросы должны возвращать ответ **менее чем за 2 секунды** на объёме ~10+ млн событий.

### 4.5 Grafana Dashboard
- Realtime график событий и DAU
- Воронка конверсии
- Retention-кривые
- Топ категорий и устройств
- Геораспределение
- Выручка по дням

## 5. Модель данных в ClickHouse

### 5.1 Основная таблица
```sql
CREATE TABLE events_raw (
    event_id UUID,
    user_id UInt64,
    session_id UUID,
    event_type LowCardinality(String),
    product_id UInt64,
    category LowCardinality(String),
    price Decimal(10,2),
    device LowCardinality(String),
    country FixedString(2),
    created_at DateTime,
    sign Int8 DEFAULT 1
) ENGINE = ReplacingMergeTree(sign)
PARTITION BY toYYYYMMDD(created_at)
ORDER BY (created_at, user_id, event_type)
TTL created_at + INTERVAL 30 DAY;
```

### 5.2 Буферная таблица
```sql
CREATE TABLE events_buffer AS events_raw 
ENGINE = Buffer('default', 'events_raw', 16, 10, 100, 10000, 100000, 1000000);
```

### 5.3 Materialized View (почасовая агрегация)
```sql
CREATE MATERIALIZED VIEW events_hourly
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(date)
ORDER BY (date, hour, event_type)
AS SELECT 
    toDate(created_at) AS date,
    toHour(created_at) AS hour,
    event_type,
    count() AS event_count,
    uniq(user_id) AS unique_users,
    sum(price) AS revenue
FROM events_raw
GROUP BY date, hour, event_type;
```

## 6. Нефункциональные требования

### 6.1 Производительность
- Ingestion: **≥ 1000 events/sec** на обычной локальной машине (цель — 1500–2000).
- Аналитические запросы (API + Grafana): **< 2 секунды** (целевой < 800 мс).
- Batch insert размером до 10 000 записей.

### 6.2 Надёжность
- Exactly-once семантика по возможности (at-least-once + deduplication).
- Retry с экспоненциальной backoff + DLQ.
- Восстановление после падения сервиса/ClickHouse/Kafka.

### 6.3 Наблюдаемость
- Логирование важных событий.
- Метрики: количество обработанных событий, latency insert, lag Kafka, ошибки (Micrometer + Prometheus).

### 6.4 Конфигурируемость
Все параметры вынесены в `application.yml` / переменные окружения (batch size, timeouts, Kafka/ClickHouse соединения и т.д.).

### 6.5 Масштабируемость
- Горизонтальное масштабирование через увеличение партиций Kafka и consumer'ов.
- Понимание шардирования ClickHouse продемонстрировано в README.
