package com.product.catalog.api;

import com.product.catalog.api.dto.CreateProductRequest;
import com.product.catalog.api.dto.UpdateProductRequest;

import com.product.catalog.api.dto.error.ConflictException;
import com.product.catalog.api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService service;

    @Test
    void create_and_get_product() {
        var created = service.create(new CreateProductRequest("Plan A", BigDecimal.valueOf(99.00), "SEK"));
        var loaded = service.get(created.getId());
        assertEquals("Plan A", loaded.getName());
        assertEquals("SEK", loaded.getCurrency());
    }

    @Test
    void duplicate_name_should_conflict_case_insensitive() {
        service.create(new CreateProductRequest("Plan B", BigDecimal.valueOf(10), "SEK"));
        assertThrows(ConflictException.class,
                () -> service.create(new CreateProductRequest("plan b", BigDecimal.valueOf(20), "SEK")));
    }

    @Test
    void update_product() {
        var created = service.create(new CreateProductRequest("Plan C-", BigDecimal.valueOf(50), "SEK"));
        var updated = service.update(created.getId(), new UpdateProductRequest("Plan C+", BigDecimal.valueOf(60), "SEK", true));
        assertEquals("Plan C+", updated.getName());
        assertEquals(BigDecimal.valueOf(60), updated.getPrice());
        assertTrue(updated.isActive());
    }
}
