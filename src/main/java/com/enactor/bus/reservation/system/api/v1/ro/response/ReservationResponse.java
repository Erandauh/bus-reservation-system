package com.enactor.bus.reservation.system.api.v1.ro.response;

import java.util.List;

public record ReservationResponse(
        String reservationNumber,
        List<String> assignedSeats,
        String origin,
        String destination,
        int totalPrice
) {}