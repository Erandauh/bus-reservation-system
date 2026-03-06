package com.enactor.bus.reservation.system.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BusTest {
    private Bus bus;

    @BeforeEach
    void setUp() {
        bus = new Bus();
    }

    @Test
    @DisplayName("Should initialize a bus with exactly 40 seats")
    void testBusInitialization_Count() {
        List<Seat> seats = bus.getSeats();

        // Standard 10 rows * 4 columns = 40
        assertThat(seats)
                .as("Bus seat inventory")
                .hasSize(40)
                .doesNotContainNull();
    }

    @Test
    @DisplayName("Should follow the Row + Letter naming convention (1A to 10D)")
    void testBusInitialization_NamingConvention() {
        List<String> labels = bus.getSeats().stream()
                .map(Seat::getLabel)
                .collect(Collectors.toList());

        // Check the first and last seats
        assertThat(labels).first().isEqualTo("1A");
        assertThat(labels).last().isEqualTo("10D");
    }
}