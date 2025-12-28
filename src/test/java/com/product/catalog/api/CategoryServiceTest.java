package com.product.catalog.api;

import com.product.catalog.api.dto.CreateCategoryRequest;
import com.product.catalog.api.dto.UpdateCategoryRequest;
import com.product.catalog.api.dto.error.ConflictException;
import com.product.catalog.api.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService service;

    @Test
    void create_and_get_category() {
        var created = service.create(new CreateCategoryRequest("Cat A", "A category"));
        var loaded = service.get(created.getId());
        assertEquals("Cat A", loaded.getName());
        assertEquals("A category", loaded.getDescription());
    }

    @Test
    void duplicate_name_should_conflict_case_insensitive() {
        service.create(new CreateCategoryRequest("Cat B", null));
        assertThrows(ConflictException.class,
                () -> service.create(new CreateCategoryRequest("cat b", null)));
    }

    @Test
    void update_category() {
        var created = service.create(new CreateCategoryRequest("Cat C", null));
        var updated = service.update(created.getId(), new UpdateCategoryRequest("Cat C+", "desc", true));
        assertEquals("Cat C+", updated.getName());
        assertEquals("desc", updated.getDescription());
        assertTrue(updated.isActive());
    }
}
