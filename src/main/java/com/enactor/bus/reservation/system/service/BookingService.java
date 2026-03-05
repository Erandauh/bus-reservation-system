package com.enactor.bus.reservation.system.service;

import java.util.List;

public interface BookingService {
    /**
     * Checks available seats
     */
    List<String> checkAvailability(String from, String to);

    /**
     * Reserves seats and returns the assigned seat labels
     */
    List<String> reserve(String from, String to, int count);

    /**
     * Calculates the price for the given journey
     */
    int calculatePrice(String from, String to, int count);
}