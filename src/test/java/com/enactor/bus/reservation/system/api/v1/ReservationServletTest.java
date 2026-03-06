package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.v1.ro.request.ReservationRequest;
import com.enactor.bus.reservation.system.service.BookingService;
import com.enactor.bus.reservation.system.service.exception.SeatNotAvailableException;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.*;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservationServletTest {

    @Mock
    private BookingService mockBookingService;

    @Spy
    private Gson gson = new Gson();

    @InjectMocks
    private ReservationServlet reservationServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("Should return 201 Created and ticket details on successful reservation")
    void testDoPost_Success() throws IOException {
        String jsonInput = "{\"origin\":\"A\",\"destination\":\"B\",\"passengerCount\":1,\"priceConfirmation\":100}";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(mockBookingService.reserve(any(ReservationRequest.class))).thenReturn(List.of("1A"));
        when(mockBookingService.calculatePrice(any(ReservationRequest.class))).thenReturn(100);

        reservationServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).setContentType("application/json");

        String output = responseWriter.toString();
        assertThat(output)
                .contains("1A")
                .contains("reservationNumber")
                .contains("\"totalPrice\":100");
    }

    @Test
    @DisplayName("Should return 409 Conflict when seats are not available")
    void testDoPost_SeatNotAvailable() throws IOException {
        String jsonInput = "{\"origin\":\"A\",\"destination\":\"B\",\"passengerCount\":5,\"priceConfirmation\":500}";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(mockBookingService.reserve(any())).thenThrow(new SeatNotAvailableException(5));

        reservationServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        assertThat(responseWriter.toString()).contains("error");
    }
}