package com.enactor.bus.reservation.system.service.exception;

// 409 Conflict
public class SeatNotAvailableException extends BookingException {
    public SeatNotAvailableException(int count) {
        super("Requested " + count + " seats are not available for this segment.", 409);
    }
}
