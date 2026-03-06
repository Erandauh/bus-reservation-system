package com.enactor.bus.reservation.system.api.v1.ro.request;

public record AvailabilityRequest(String origin, String destination, int passengerCount) {
}
