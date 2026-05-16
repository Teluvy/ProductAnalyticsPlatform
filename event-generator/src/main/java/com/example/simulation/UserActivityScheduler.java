package com.example.simulation;

import com.example.model.UserEvent;
import com.example.model.VirtualUser;
import com.example.producer.KafkaEventProducer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserActivityScheduler {

    private final List<VirtualUser> users;
    private final SessionSimulator simulator;
    private final KafkaEventProducer producer;

    public UserActivityScheduler(
            List<VirtualUser> users,
            SessionSimulator simulator,
            KafkaEventProducer producer
    ) {
        this.users = users;
        this.simulator = simulator;
        this.producer = producer;
    }

    public void tick() {

        users.stream()
                .filter(this::isActiveNow)
                .forEach(this::simulateUser);
    }

    private boolean isActiveNow(VirtualUser user) {

        return ThreadLocalRandom.current()
                .nextDouble() < user.getActivityScore();
    }

    private void simulateUser(VirtualUser user) {

        List<UserEvent> events =
                simulator.simulate(user);

        events.forEach(producer::send);
    }
}
