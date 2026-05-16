package com.example.config;

public class GeneratorConfig {

    private final String kafkaBootstrapServers;
    private final String topic;
    private final int usersCount;
    private final int schedulerThreads;
    private final int tickRateMs;

    public GeneratorConfig(
            String kafkaBootstrapServers,
            String topic,
            int usersCount,
            int schedulerThreads,
            int tickRateMs
    ) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.topic = topic;
        this.usersCount = usersCount;
        this.schedulerThreads = schedulerThreads;
        this.tickRateMs = tickRateMs;
    }

    public static GeneratorConfig load() {

        return new GeneratorConfig(
                env("KAFKA_BOOTSTRAP", "localhost:9092"),
                env("KAFKA_TOPIC", "user-events"),
                Integer.parseInt(env("USERS_COUNT", "100000")),
                Integer.parseInt(env("SCHEDULER_THREADS", "4")),
                Integer.parseInt(env("TICK_RATE_MS", "1000"))
        );
    }

    private static String env(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }

    public String kafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public String topic() {
        return topic;
    }

    public int usersCount() {
        return usersCount;
    }

    public int schedulerThreads() {
        return schedulerThreads;
    }

    public int tickRateMs() {
        return tickRateMs;
    }
}