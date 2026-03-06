package com.enactor.bus.reservation.system.service.exception;

// Base Exception
public abstract class BookingException extends RuntimeException {
    private final int statusCode;
    public BookingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public int getStatusCode() { return statusCode; }
}