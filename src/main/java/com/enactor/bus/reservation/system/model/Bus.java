package com.enactor.bus.reservation.system.model;

import java.util.ArrayList;
import java.util.List;

public class Bus {
    private final List<Seat> seats = new ArrayList<>();

    public Bus() {
        for (int row = 1; row <= 10; row++) {
            for (char col = 'A'; col <= 'D'; col++) {
                seats.add(new Seat(row + String.valueOf(col)));
            }
        }
    }

    public List<Seat> getSeats() {
        return seats;
    }
}