package com.enactor.bus.reservation.system.service.exception;

// 400 Bad Request
public class InvalidRouteException extends BookingException {
    public InvalidRouteException(String from, String to) {
        super("Invalid route from " + from + " to " + to, 400);
    }
}
