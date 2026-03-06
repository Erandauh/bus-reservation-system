package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.v1.ro.request.ReservationRequest;
import com.enactor.bus.reservation.system.api.v1.ro.response.ReservationResponse;
import com.enactor.bus.reservation.system.service.BookingService;
import com.enactor.bus.reservation.system.service.exception.InvalidPriceException;
import com.enactor.bus.reservation.system.service.exception.InvalidRouteException;
import com.enactor.bus.reservation.system.service.exception.SeatNotAvailableException;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/v1/reserve")
public class ReservationServlet extends HttpServlet {
    private BookingService bookingService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.bookingService = (BookingService) getServletContext().getAttribute("bookingService");
        this.gson = (Gson) getServletContext().getAttribute("gson");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 1. Read JSON body from the request
            StringBuilder buffer = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            }

            // 2. Deserialize JSON to Record DTO
            ReservationRequest reservationRequest = gson.fromJson(buffer.toString(), ReservationRequest.class);

            // 3. Call Business Logic
            List<String> reservedSeats = bookingService.reserve(reservationRequest);

            // 4. Handle Success
            int totalPrice = bookingService.calculatePrice(reservationRequest);
            String ticketId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            ReservationResponse reservationResponse = new ReservationResponse(
                    ticketId,
                    reservedSeats,
                    reservationRequest.origin(),
                    reservationRequest.destination(),
                    totalPrice
            );

            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            resp.getWriter().write(gson.toJson(reservationResponse));

        } catch (InvalidRouteException | InvalidPriceException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        } catch (SeatNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict is standard for this
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            resp.getWriter().write(gson.toJson(Map.of("error", "Internal Server Error")));
        }
    }
}