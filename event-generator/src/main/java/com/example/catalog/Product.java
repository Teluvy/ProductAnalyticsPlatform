package com.example.catalog;

import java.math.BigDecimal;

public record Product(
        long productId,
        String category,
        BigDecimal price,
        double popularityScore
) {}
