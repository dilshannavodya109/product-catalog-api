package com.product.catalog.api.dto;

import com.product.catalog.api.validation.CurrencyCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotBlank @CurrencyCode String currency
) {}
