package com.example.simulation;

import com.example.catalog.Product;
import com.example.catalog.ProductCatalog;
import com.example.factory.EventFactory;
import com.example.model.EventType;
import com.example.model.UserEvent;
import com.example.model.VirtualUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionSimulator {

    private final FunnelEngine funnelEngine;
    private final EventFactory eventFactory;
    private final ProductCatalog productCatalog;

    public SessionSimulator(
            FunnelEngine funnelEngine,
            EventFactory eventFactory,
            ProductCatalog productCatalog
    ) {
        this.funnelEngine = funnelEngine;
        this.eventFactory = eventFactory;
        this.productCatalog = productCatalog;
    }

    public List<UserEvent> simulate(VirtualUser user) {

        UUID sessionId = UUID.randomUUID();

        List<EventType> funnel =
                funnelEngine.generateFunnel(user);

        List<UserEvent> events = new ArrayList<>();

        for (EventType type : funnel) {

            Product product =
                    requiresProduct(type)
                            ? productCatalog.getRandomPopularProduct()
                            : null;

            UserEvent event =
                    eventFactory.createEvent(
                            user,
                            sessionId,
                            type,
                            product
                    );

            events.add(event);
        }

        return events;
    }

    private boolean requiresProduct(EventType type) {

        return switch (type) {
            case VIEW_PRODUCT,
                 ADD_TO_CART,
                 BUY_PRODUCT,
                 ADD_TO_FAVORITE -> true;

            default -> false;
        };
    }
}
