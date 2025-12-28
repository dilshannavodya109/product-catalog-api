package com.product.catalog.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        String currency,
        boolean active,
        Instant createdAt
) {}
