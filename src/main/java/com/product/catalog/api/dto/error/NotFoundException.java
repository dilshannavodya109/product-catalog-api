package com.product.catalog.api.dto.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) { super(msg); }
}
