package com.example.simulation;

import com.example.model.VirtualUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserFactory {

    private static final String[] COUNTRIES = {
            "US",
            "DE",
            "FR",
            "IN",
            "RU"
    };

    private static final String[] DEVICES = {
            "mobile",
            "desktop",
            "tablet"
    };

    public List<VirtualUser> generateUsers(int count) {

        List<VirtualUser> users = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            String country = COUNTRIES[
                    ThreadLocalRandom.current()
                            .nextInt(COUNTRIES.length)
                    ];

            String device = DEVICES[
                    ThreadLocalRandom.current()
                            .nextInt(DEVICES.length)
                    ];

            double purchaseProbability =
                    ThreadLocalRandom.current()
                            .nextDouble(0.05, 0.25);

            double activityScore =
                    ThreadLocalRandom.current()
                            .nextDouble(0.01, 0.05);

            users.add(
                    new VirtualUser(
                            i,
                            country,
                            device,
                            purchaseProbability,
                            activityScore
                    )
            );
        }

        return users;
    }
}
