package com.enactor.bus.reservation.system.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeatTest {

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat("A1");
    }

    @Test
    @DisplayName("Should successfully reserve and block specific segments")
    void testReserveSegments() {
        seat.reserve(0, 1, false);

        // Assert: A-B should be taken, but B-D should still be free
        assertThat(seat.isAvailable(0, 1, false)).as("Segment A-B").isFalse();
        assertThat(seat.isAvailable(1, 3, false)).as("Segments B-D").isTrue();
    }

    @Test
    @DisplayName("Should be available when no segments are reserved")
    void testInitialAvailability() {
        // Checking full trip A to D (segments 0, 1, 2)
        assertThat(seat.isAvailable(0, 3, false)).isTrue();
        // Checking full return trip D to A (segments 0, 1, 2)
        assertThat(seat.isAvailable(0, 3, true)).isTrue();
    }

    @Test
    @DisplayName("Outbound reservation should not affect return availability")
    void testOutboundVsReturnIsolation() {
        seat.reserve(0, 3, false);

        assertThat(seat.isAvailable(0, 3, false)).isFalse();

        // Assert: Return trip is still completely available
        assertThat(seat.isAvailable(0, 3, true))
                .as("Return segments should be independent of outbound")
                .isTrue();
    }

    @Test
    @DisplayName("Return reservation should not affect outbound availability")
    void testOutboundVsReturnIsolation2() {
        seat.reserve(0, 3, true);

        assertThat(seat.isAvailable(0, 3, true)).isFalse();

        // Assert: Outbound trip is still completely available
        assertThat(seat.isAvailable(0, 3, false))
                .as("Outbound segments should be independent of return")
                .isTrue();
    }
}