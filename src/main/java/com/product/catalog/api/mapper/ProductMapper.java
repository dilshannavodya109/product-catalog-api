package com.product.catalog.api.mapper;

import com.product.catalog.api.dto.ProductResponse;
import com.product.catalog.api.dto.domain.Product;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product p) {
        if (p == null) return null;
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getCurrency(),
                p.isActive(),
                p.getCreatedAt()
        );
    }
}
