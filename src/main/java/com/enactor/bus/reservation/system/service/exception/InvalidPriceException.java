package com.enactor.bus.reservation.system.service.exception;

// 400 Bad Request
public class InvalidPriceException extends BookingException {
    public InvalidPriceException(int priceExpected, int priceEntered) {
        super("Invalid price entered " + priceEntered + "! expected price is " + priceExpected, 400);
    }
}
