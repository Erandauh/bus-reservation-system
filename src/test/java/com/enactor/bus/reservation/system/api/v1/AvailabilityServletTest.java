package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.v1.ro.request.AvailabilityRequest;
import com.enactor.bus.reservation.system.service.BookingService;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AvailabilityServletTest {

    @Mock
    private BookingService mockBookingService;

    @Spy
    private Gson gson = new Gson();

    @InjectMocks
    private AvailabilityServlet availabilityServlet;

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
    @DisplayName("Should return availability and price for valid origin and destination")
    void testDoGet_Success() throws IOException {
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("C");
        when(request.getParameter("passengers")).thenReturn("2");
        List<String> mockSeats = List.of("1A", "1B");
        when(mockBookingService.checkAvailability(any(AvailabilityRequest.class))).thenReturn(mockSeats);
        when(mockBookingService.calculatePrice(any(AvailabilityRequest.class))).thenReturn(200);

        availabilityServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertThat(jsonResponse)
                .contains("\"origin\":\"A\"")
                .contains("\"destination\":\"C\"")
                .contains("1A", "1B");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when origin or destination is missing")
    void testDoGet_MissingParameters() throws IOException {
        when(request.getParameter("origin")).thenReturn(null);
        when(request.getParameter("destination")).thenReturn("C");

        availabilityServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(responseWriter.toString()).contains("Missing origin or destination");
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid passenger format")
    void testDoGet_InvalidPassengerFormat() throws IOException {
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("C");
        when(request.getParameter("passengers")).thenReturn("not-a-number");

        availabilityServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(responseWriter.toString()).contains("Invalid passenger count format");
    }
}