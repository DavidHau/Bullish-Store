package com.bullish.store.facade.common;

import com.bullish.store.common.exception.DataInconsistentException;
import com.bullish.store.common.exception.ProductNotFoundException;
import com.bullish.store.common.exception.ProductOnSaleLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(DataInconsistentException.class)
    public ResponseEntity<String> generateDataInconsistentExceptionResponse(
        DataInconsistentException exception) {
        return new ResponseEntity<>(exception.getMessage(),
            HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ProductOnSaleLockException.class)
    public ResponseEntity<String> generateProductOnSaleLockExceptionResponse(
        ProductOnSaleLockException exception) {
        return new ResponseEntity<>(exception.getMessage(),
            HttpStatus.LOCKED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> generateProductNotFoundExceptionResponse(
        ProductNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(),
            HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> generateIllegalArgumentExceptionResponse(
        IllegalArgumentException exception) {
        return new ResponseEntity<>(exception.getMessage(),
            HttpStatus.BAD_REQUEST);
    }

}