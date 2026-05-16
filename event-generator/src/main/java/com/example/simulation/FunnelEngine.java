package com.example.simulation;

import com.example.model.EventType;
import com.example.model.VirtualUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FunnelEngine {

    public List<EventType> generateFunnel(VirtualUser user) {

        List<EventType> events = new ArrayList<>();

        events.add(EventType.OPEN_APP);

        if (chance(0.80)) {
            events.add(EventType.SEARCH);
        }

        if (chance(0.70)) {
            events.add(EventType.VIEW_PRODUCT);
        }

        if (chance(0.30)) {
            events.add(EventType.ADD_TO_CART);
        }

        if (chance(0.15)) {
            events.add(EventType.ADD_TO_FAVORITE);
        }

        if (chance(user.getPurchaseProbability())) {
            events.add(EventType.BUY_PRODUCT);
        }

        events.add(EventType.LOGOUT);

        return events;
    }

    private boolean chance(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }
}
