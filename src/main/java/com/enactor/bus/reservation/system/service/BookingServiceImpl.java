package com.enactor.bus.reservation.system.service;

import com.enactor.bus.reservation.system.api.v1.ro.request.AvailabilityRequest;
import com.enactor.bus.reservation.system.api.v1.ro.request.ReservationRequest;
import com.enactor.bus.reservation.system.model.Bus;
import com.enactor.bus.reservation.system.model.Seat;
import com.enactor.bus.reservation.system.service.exception.InvalidPriceException;
import com.enactor.bus.reservation.system.service.exception.InvalidRouteException;
import com.enactor.bus.reservation.system.service.exception.SeatNotAvailableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {
    private final Bus bus;
    private final List<String> ROUTE = Arrays.asList("A", "B", "C", "D");
    private final PricingStrategy pricingStrategy;

    public BookingServiceImpl(Bus bus, PricingStrategy pricingStrategy) {
        this.bus = bus;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Check seat availabilty for a segment
     */
    @Override
    public List<String> checkAvailability(AvailabilityRequest availabilityRequest) {
        String from = availabilityRequest.origin();
        String to = availabilityRequest.destination();

        boolean isReturn = isReturnJourney(from, to);
        int start = getSegmentIndex(from, isReturn);
        int end = getSegmentIndex(to, isReturn);

        return bus.getSeats().stream()
                .filter(s -> s.isAvailable(start, end, isReturn))
                .map(Seat::getLabel)
                .collect(Collectors.toList());
    }

    /**
     * Orchestrates the reservation process.
     * Synchronized here to ensure thread-safety across the whole bus inventory.
     */
    @Override
    public synchronized List<String> reserve(ReservationRequest reservationRequest) {
        String from = reservationRequest.origin();
        String to = reservationRequest.destination();
        int passengersCount = reservationRequest.passengerCount();

        if (!isValid(from, to)) {
            throw new InvalidRouteException(from, to);
        }

        // validate the price entered by user
        int expectedPrice = calculatePrice(reservationRequest);
        if (expectedPrice != reservationRequest.priceConfirmation()) {
            throw new InvalidPriceException(expectedPrice, reservationRequest.priceConfirmation());
        }

        boolean isReturn = isReturnJourney(from, to);
        int start = getSegmentIndex(from, isReturn);
        int end= getSegmentIndex(to, isReturn);

        List<Seat> available = new ArrayList<>();
        for (Seat seat : bus.getSeats()) {
            if (seat.isAvailable(start, end, isReturn)) {
                available.add(seat);
            }
            if (available.size() == passengersCount) break;
        }

        // validate seats available enough per booking requirment
        if (available.size() < passengersCount) {
            throw new SeatNotAvailableException(passengersCount);
        }

        if (available.size() == passengersCount) {
            available.forEach(s -> s.reserve(start, end, isReturn));
            return available.stream().map(Seat::getLabel).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public int calculatePrice(AvailabilityRequest availabilityRequest) {
        return pricingStrategy.calculate(this.ROUTE, availabilityRequest.origin(),
                availabilityRequest.destination(), availabilityRequest.passengerCount());
    }

    @Override
    public int calculatePrice(ReservationRequest reservationRequest) {
        return pricingStrategy.calculate(this.ROUTE, reservationRequest.origin(),
                reservationRequest.destination(), reservationRequest.passengerCount());
    }

    public boolean isReturnJourney(String from, String to) {
        return ROUTE.indexOf(from) > ROUTE.indexOf(to);
    }

    private int getSegmentIndex(String stop, boolean isReturn) {
        int index = ROUTE.indexOf(stop);
        return isReturn ? (ROUTE.size() - 1) - index : index;
    }

    private boolean isValid(String from, String to) {
        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        int startIndex = ROUTE.indexOf(from);
        int endIndex = ROUTE.indexOf(to);

        return startIndex != -1 && endIndex != -1;
    }
}