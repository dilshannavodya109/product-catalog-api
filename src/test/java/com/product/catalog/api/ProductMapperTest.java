package com.product.catalog.api;

import com.product.catalog.api.dto.ProductResponse;
import com.product.catalog.api.dto.domain.Product;
import com.product.catalog.api.mapper.ProductMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void toResponse_mapsFieldsCorrectly() {
        Product p = new Product("Plan X", BigDecimal.valueOf(123.45), "SEK");

        ProductResponse resp = ProductMapper.toResponse(p);

        assertNotNull(resp);
        assertEquals(p.getName(), resp.name());
        assertEquals(p.getPrice(), resp.price());
        assertEquals(p.getCurrency(), resp.currency());
        assertEquals(p.isActive(), resp.active());
        assertEquals(p.getId(), resp.id());
        assertEquals(p.getCreatedAt(), resp.createdAt());
    }

    @Test
    void toResponse_returnsNullForNullInput() {
        assertNull(ProductMapper.toResponse(null));
    }
}
