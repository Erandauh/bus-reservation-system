package com.enactor.bus.reservation.system.service;

import com.enactor.bus.reservation.system.model.Bus;
import com.enactor.bus.reservation.system.model.Seat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {
    private final Bus bus;
    private final List<String> route = Arrays.asList("A", "B", "C", "D");
    private static final int PRICE_PER_STOP = 50;

    public BookingServiceImpl(Bus bus) {
        this.bus = bus;
    }

    /**
     * Check seat availabilty for a segment
     */
    @Override
    public List<String> checkAvailability(String from, String to) {
        boolean isReturn = isReturnJourney(from, to);
        int start = getSegmentIndex(from, isReturn);
        int end = getSegmentIndex(to, isReturn);

        return bus.getSeats().stream()
                .filter(s -> s.isAvailable(start, end, isReturn))
                .map(Seat::getLabel)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> reserve(String from, String to, int count) {
        return List.of();
    }

    @Override
    public int calculatePrice(String from, String to, int count) {
        int stops = Math.abs(route.indexOf(to) - route.indexOf(from));
        return stops * PRICE_PER_STOP * count;
    }

    public boolean isReturnJourney(String from, String to) {
        return route.indexOf(from) > route.indexOf(to);
    }

    private int getSegmentIndex(String stop, boolean isReturn) {
        int index = route.indexOf(stop);
        return isReturn ? (route.size() - 1) - index : index;
    }
}