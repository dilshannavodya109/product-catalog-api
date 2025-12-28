package com.product.catalog.api.controller;

import com.product.catalog.api.dto.CategoryResponse;
import com.product.catalog.api.dto.CreateCategoryRequest;
import com.product.catalog.api.dto.UpdateCategoryRequest;
import com.product.catalog.api.dto.domain.Category;
import com.product.catalog.api.mapper.CategoryMapper;
import com.product.catalog.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(CategoryController.BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    public static final String BASE_URL = "/api/v1/categories";

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest req,
            UriComponentsBuilder ucb
    ) {
        log.info("POST /categories create name={}", req.name());
        Category category = service.create(req);
        return ResponseEntity
                .created(ucb.path(CategoryController.BASE_URL + "/{id}").buildAndExpand(category.getId()).toUri())
                .body(CategoryMapper.toResponse(category));
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable("id") Long id) {
        log.info("GET /categories/{}", id);
        return CategoryMapper.toResponse(service.get(id));
    }

    @GetMapping
    public Page<CategoryResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /categories list pageSize={} pageNumber={}", pageable.getPageSize(), pageable.getPageNumber());
        return service.list(pageable).map(CategoryMapper::toResponse);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateCategoryRequest req
    ) {
        log.info("PUT /categories/{} update name={}", id, req.name());
        return CategoryMapper.toResponse(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("DELETE /categories/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
