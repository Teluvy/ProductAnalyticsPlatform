package com.example.factory;

import com.example.catalog.Product;
import com.example.model.EventType;
import com.example.model.UserEvent;
import com.example.model.VirtualUser;

import java.time.Instant;
import java.util.UUID;

public class EventFactory {

    public UserEvent createEvent(
            VirtualUser user,
            UUID sessionId,
            EventType type,
            Product product
    ) {

        return new UserEvent(
                UUID.randomUUID(),
                user.getUserId(),
                sessionId,
                type,
                product != null ? product.productId() : null,
                product != null ? product.category() : null,
                product != null ? product.price() : null,
                user.getDevice(),
                user.getCountry(),
                Instant.now()
        );
    }
}