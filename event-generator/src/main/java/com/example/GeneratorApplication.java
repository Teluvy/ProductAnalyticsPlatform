package com.example;

import com.example.catalog.ProductCatalog;
import com.example.config.GeneratorConfig;
import com.example.factory.EventFactory;
import com.example.model.VirtualUser;
import com.example.producer.KafkaEventProducer;
import com.example.simulation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GeneratorApplication {

    private static final Logger log =
            LoggerFactory.getLogger(GeneratorApplication.class);

    public static void main(String[] args) {

        GeneratorConfig config = GeneratorConfig.load();

        log.info("Starting Event Generator...");

        ProductCatalog productCatalog =
                ProductCatalog.demoCatalog();

        UserFactory userFactory = new UserFactory();

        List<VirtualUser> users =
                userFactory.generateUsers(
                        config.usersCount()
                );

        EventFactory eventFactory = new EventFactory();

        FunnelEngine funnelEngine = new FunnelEngine();

        SessionSimulator simulator =
                new SessionSimulator(
                        funnelEngine,
                        eventFactory,
                        productCatalog
                );

        KafkaEventProducer producer =
                new KafkaEventProducer(
                        config.kafkaBootstrapServers(),
                        config.topic()
                );

        UserActivityScheduler scheduler =
                new UserActivityScheduler(
                        users,
                        simulator,
                        producer
                );

        SimulationEngine engine =
                new SimulationEngine(
                        config.schedulerThreads(),
                        config.tickRateMs(),
                        scheduler
                );

        engine.start();

        log.info("Generator started successfully");
    }
}