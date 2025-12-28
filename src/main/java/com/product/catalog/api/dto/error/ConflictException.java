package com.product.catalog.api.dto.error;

public class ConflictException extends RuntimeException {
    public ConflictException(String msg) { super(msg); }
}
