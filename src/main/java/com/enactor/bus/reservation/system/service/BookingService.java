package com.enactor.bus.reservation.system.service;

import com.enactor.bus.reservation.system.api.v1.ro.request.AvailabilityRequest;
import com.enactor.bus.reservation.system.api.v1.ro.request.ReservationRequest;

import java.util.List;

public interface BookingService {
    /**
     * Checks available seats
     */
    List<String> checkAvailability(AvailabilityRequest availabilityRequest);

    /**
     * Reserves seats and returns the assigned seat labels
     */
    List<String> reserve(ReservationRequest reservationRequest);

    /**
     * Calculates the price for the given journey (applicable for both scenarios)
     */
    int calculatePrice(AvailabilityRequest availabilityRequest);
    int calculatePrice(ReservationRequest reservationRequest);
}