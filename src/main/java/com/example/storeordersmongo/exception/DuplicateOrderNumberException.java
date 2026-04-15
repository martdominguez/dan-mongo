package com.example.storeordersmongo.exception;

public class DuplicateOrderNumberException extends RuntimeException {

    public DuplicateOrderNumberException(String orderNumber) {
        super("Order number '%s' already exists".formatted(orderNumber));
    }
}
