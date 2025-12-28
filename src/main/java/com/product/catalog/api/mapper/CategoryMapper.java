package com.product.catalog.api.mapper;

import com.product.catalog.api.dto.CategoryResponse;
import com.product.catalog.api.dto.domain.Category;

public final class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category c) {
        if (c == null) return null;
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.isActive(),
                c.getCreatedAt()
        );
    }
}
