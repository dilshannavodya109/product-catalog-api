package com.product.catalog.api.controller;

import com.product.catalog.api.dto.CreateProductRequest;
import com.product.catalog.api.dto.ProductResponse;
import com.product.catalog.api.dto.UpdateProductRequest;
import com.product.catalog.api.dto.domain.Product;
import com.product.catalog.api.mapper.ProductMapper;
import com.product.catalog.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(ProductController.BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    public static final String BASE_URL = "/api/v1/products";

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest req,
            UriComponentsBuilder ucb
    ) {
        log.info("POST /products create name={}", req.name());
        Product product = service.create(req);
        return ResponseEntity
            .created(ucb.path(ProductController.BASE_URL + "/{id}").buildAndExpand(product.getId()).toUri())
            .body(ProductMapper.toResponse(product));
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable("id") Long id) {
        return ProductMapper.toResponse(service.get(id));
    }

    @GetMapping
    public Page<ProductResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable).map(ProductMapper::toResponse);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductRequest req
    ) {
        log.info("PUT /products/{} update name={}", id, req.name());
        return ProductMapper.toResponse(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("DELETE /products/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
