package com.product.catalog.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import com.product.catalog.api.validation.CurrencyCode;

public record UpdateProductRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotBlank @CurrencyCode String currency,
        @NotNull Boolean active
) {}