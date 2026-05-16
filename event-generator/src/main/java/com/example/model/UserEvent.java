package com.example.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UserEvent(
        UUID eventId,
        long userId,
        UUID sessionId,
        EventType eventType,
        Long productId,
        String category,
        BigDecimal price,
        String device,
        String country,
        Instant createdAt
) {}
