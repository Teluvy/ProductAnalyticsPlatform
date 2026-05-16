# Кафка
`
docker compose up -d
`

`
docker ps
`

ищем id контейнера с кафкой

входим в него по id:

`
docker exec -it <kafka_container_id> bash
`

`
docker exec -it 5928865a7941 bash
`

Создаем топик:

`
kafka-topics \
  --create \
  --topic user-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
`


Проверяем топик:
`
kafka-topics \
  --list \
  --bootstrap-server localhost:9092
`

Ожидаемый вывод: user-events (который мы создали выше)

# Сборка сервиса
mvn clean package

# Запуск
java -jar target/ProductAnalyticsPlatform-1.0-SNAPSHOT-jar-with-dependencies.jar


# Пример данных в консьюмере:

{

"eventId":"3c539c94-e8e5-41cb-b2cd-09d025b94d3b",

"userId":99955,

"sessionId":"8dde34a1-6669-42a8-9fe7-66fb6fa4c96d",

"eventType":"ADD_TO_CART",

"productId":1199,

"category":"electronics",

"price":1344.7870914993953,

"device":"tablet",

"country":"US",

"createdAt":1778971962.125305290

}

## Для входа в консьюмер внутри кафки:

kafka-console-consumer \
--topic user-events \
--bootstrap-server localhost:9092