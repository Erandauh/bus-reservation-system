package com.enactor.bus.reservation.system.api.v1.ro.response;

import java.util.List;

public record AvailabilityResponse(
        List<String> availableSeats,
        int totalPrice,
        String origin,
        String destination
) {}