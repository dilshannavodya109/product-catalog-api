package com.product.catalog.api.service;

import com.product.catalog.api.dto.CreateProductRequest;
import com.product.catalog.api.dto.UpdateProductRequest;

import com.product.catalog.api.dto.domain.Product;
import com.product.catalog.api.dto.error.ConflictException;
import com.product.catalog.api.dto.error.NotFoundException;
import com.product.catalog.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repository;

    @Transactional
    public Product create(CreateProductRequest req) {
        log.info("Creating product name={}", req.name());
        if (repository.existsByNameIgnoreCase(req.name())) {
            log.warn("Conflict creating product, name already exists: {}", req.name());
            throw new ConflictException("Product already exists");
        }
        Product saved = repository.save(new Product(req.name(), req.price(), req.currency()));
        log.info("Created product id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        log.info("Fetching product id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Transactional(readOnly = true)
    public Page<Product> list(Pageable pageable) {
        log.info("Listing products pageSize={} pageNumber={}", pageable.getPageSize(), pageable.getPageNumber());
        return repository.findAll(pageable);
    }

    @Transactional
    public Product update(Long id, UpdateProductRequest req) {
        log.info("Updating product id={} name={}", id, req.name());
        Product product = get(id);

        if (!product.getName().equalsIgnoreCase(req.name())
                && repository.existsByNameIgnoreCase(req.name())) {
            log.warn("Conflict updating product id={}, name already exists: {}", id, req.name());
            throw new ConflictException("Product already exists");
        }

        product.update(req.name(), req.price(), req.currency(), req.active());
        log.info("Updated product id={} name={}", product.getId(), product.getName());
        return product;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
        log.info("Deleted product id={}", id);
    }
}