package com.bullish.store.common.exception;

public class ProductOnSaleLockException extends RuntimeException {
    public ProductOnSaleLockException(String message) {
        super(message);
    }
}