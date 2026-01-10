package com.cinebook.service;

public class OrderException extends RuntimeException {
    private final int statusCode;

    public OrderException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
