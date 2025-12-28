package com.product.catalog.api;

import com.product.catalog.api.dto.CategoryResponse;
import com.product.catalog.api.dto.domain.Category;
import com.product.catalog.api.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    @Test
    void toResponse_mapsFieldsCorrectly() {
        Category c = new Category("CatTest", "A test category");

        CategoryResponse resp = CategoryMapper.toResponse(c);

        assertNotNull(resp);
        assertEquals(c.getName(), resp.name());
        assertEquals(c.getDescription(), resp.description());
        assertEquals(c.isActive(), resp.active());
        assertEquals(c.getId(), resp.id());
        assertEquals(c.getCreatedAt(), resp.createdAt());
    }

    @Test
    void toResponse_returnsNullForNullInput() {
        assertNull(CategoryMapper.toResponse(null));
    }
}
