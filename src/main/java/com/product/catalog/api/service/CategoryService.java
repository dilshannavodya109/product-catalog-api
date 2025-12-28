package com.product.catalog.api.service;

import com.product.catalog.api.dto.CreateCategoryRequest;
import com.product.catalog.api.dto.UpdateCategoryRequest;
import com.product.catalog.api.dto.domain.Category;
import com.product.catalog.api.dto.error.ConflictException;
import com.product.catalog.api.dto.error.NotFoundException;
import com.product.catalog.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository repository;

    @Transactional
    public Category create(CreateCategoryRequest req) {
        log.info("Creating category name={}", req.name());
        if (repository.existsByNameIgnoreCase(req.name())) {
            log.warn("Conflict creating category, name already exists: {}", req.name());
            throw new ConflictException("Category already exists");
        }
        Category saved = repository.save(new Category(req.name(), req.description()));
        log.info("Created category id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional(readOnly = true)
    public Category get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Transactional(readOnly = true)
    public Page<Category> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Category update(Long id, UpdateCategoryRequest req) {
        log.info("Updating category id={} name={}", id, req.name());
        Category category = get(id);

        if (!category.getName().equalsIgnoreCase(req.name())
                && repository.existsByNameIgnoreCase(req.name())) {
            log.warn("Conflict updating category id={}, name already exists: {}", id, req.name());
            throw new ConflictException("Category already exists");
        }

        category.update(req.name(), req.description(), req.active());
        log.info("Updated category id={} name={}", category.getId(), category.getName());
        return category;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
        log.info("Deleted category id={}", id);
    }
}
