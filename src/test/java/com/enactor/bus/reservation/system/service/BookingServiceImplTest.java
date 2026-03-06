package com.enactor.bus.reservation.system.service;

import com.enactor.bus.reservation.system.api.v1.ro.request.AvailabilityRequest;
import com.enactor.bus.reservation.system.api.v1.ro.request.ReservationRequest;
import com.enactor.bus.reservation.system.model.Bus;
import com.enactor.bus.reservation.system.model.Seat;
import com.enactor.bus.reservation.system.service.exception.InvalidPriceException;
import com.enactor.bus.reservation.system.service.exception.SeatNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private Bus mockBus;
    private PricingStrategy mockPricingStrategy;
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        mockBus = mock(Bus.class);
        mockPricingStrategy = mock(PricingStrategy.class);

        bookingService = new BookingServiceImpl(mockBus, mockPricingStrategy);
    }

    @Test
    @DisplayName("Should return labels of seats that are available for a given segment")
    void testCheckAvailability_Success() {
        AvailabilityRequest request = new AvailabilityRequest("A", "C", 1);

        Seat availableSeat = mock(Seat.class);
        when(availableSeat.getLabel()).thenReturn("A1");
        // For A to C (Forward), start index is 0, end is 2
        when(availableSeat.isAvailable(0, 2, false)).thenReturn(true);

        Seat takenSeat = mock(Seat.class);
        when(takenSeat.getLabel()).thenReturn("A2");
        when(takenSeat.isAvailable(0, 2, false)).thenReturn(false);

        when(mockBus.getSeats()).thenReturn(Arrays.asList(availableSeat, takenSeat));

        List<String> result = bookingService.checkAvailability(request);

        assertThat(result)
                .as("Check available seats for segment A to C")
                .hasSize(1)
                .containsExactly("A1")
                .doesNotContain("A2");
        verify(availableSeat).isAvailable(0, 2, false);
        verify(takenSeat).isAvailable(0, 2, false);
    }

    @Test
    @DisplayName("Should successfully reserve seats when price is correct and seats are available")
    void testReserve_Success() {
        ReservationRequest request = new ReservationRequest("A", "B", 1, 100);
        when(mockPricingStrategy.calculate(any(), anyString(), anyString(), anyInt())).thenReturn(100);

        Seat mockSeat = mock(Seat.class);
        when(mockSeat.getLabel()).thenReturn("A1");
        when(mockSeat.isAvailable(0, 1, false)).thenReturn(true);
        when(mockBus.getSeats()).thenReturn(Arrays.asList(mockSeat));

        List<String> reservedSeats = bookingService.reserve(request);

        assertThat(reservedSeats)
                .as("Reserved seats list")
                .isNotNull()
                .hasSize(1)
                .containsExactly("A1");

        verify(mockSeat).reserve(0, 1, false);
    }

    @Test
    @DisplayName("Should throw InvalidPriceException if the confirmed price does not match expected price")
    void testReserve_WrongPrice() {
        ReservationRequest request = new ReservationRequest("A", "B", 1, 50);

        when(mockPricingStrategy.calculate(any(), anyString(), anyString(), anyInt())).thenReturn(100);

        assertThatThrownBy(() -> bookingService.reserve(request))
                .isInstanceOf(InvalidPriceException.class)
                .hasMessageContaining("100")
                .hasMessageContaining("50");
    }

    @Test
    @DisplayName("Should throw SeatNotAvailableException if not enough seats are found")
    void testReserve_NotEnoughSeats() {
        ReservationRequest request = new ReservationRequest("A", "B", 2, 200);
        when(mockPricingStrategy.calculate(any(), anyString(), anyString(), anyInt())).thenReturn(200);

        Seat mockSeat = mock(Seat.class);
        when(mockSeat.isAvailable(anyInt(), anyInt(), anyBoolean())).thenReturn(false);
        when(mockBus.getSeats()).thenReturn(Arrays.asList(mockSeat));

        assertThatExceptionOfType(SeatNotAvailableException.class)
                .isThrownBy(() -> bookingService.reserve(request))
                .withMessageContaining("2");
    }

    @Test
    @DisplayName("Should delegate price calculation to PricingStrategy for AvailabilityRequest")
    void calculatePrice_AvailabilityRequest() {
        int expected = 200;
        AvailabilityRequest availabilityRequest = new AvailabilityRequest("A", "C", 2);

        when(mockPricingStrategy.calculate(eq(Arrays.asList("A", "B", "C", "D")),
                eq("A"), eq("C"), eq(2))).thenReturn(200);

        int actualPrice = bookingService.calculatePrice(availabilityRequest);

        assertThat(actualPrice).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should delegate price calculation to PricingStrategy for ReservationRequest")
    void testCalculatePrice_ReservationRequest() {
        ReservationRequest request = new ReservationRequest("B", "D", 3, 500);
        int serverCalculatedPrice = 450;

        when(mockPricingStrategy.calculate(anyList(), eq("B"), eq("D"), eq(3)))
                .thenReturn(serverCalculatedPrice);

        int actualPrice = bookingService.calculatePrice(request);

        assertThat(actualPrice)
                .as("Calculated price for ReservationRequest")
                .isEqualTo(serverCalculatedPrice);
    }

    @Test
    @DisplayName("Should correctly identify a return journey")
    void testIsReturnJourney() {
        // A -> C is outbound (0 -> 2)
        assertFalse(bookingService.isReturnJourney("A", "C"));

        // C -> A is return (2 -> 0)
        assertTrue(bookingService.isReturnJourney("C", "A"));
    }
}