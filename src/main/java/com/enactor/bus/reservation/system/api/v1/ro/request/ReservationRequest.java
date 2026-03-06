package com.enactor.bus.reservation.system.api.v1.ro.request;

public record ReservationRequest(
        String origin,
        String destination,
        int passengerCount,
        int priceConfirmation
) {}