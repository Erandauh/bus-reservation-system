package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.v1.ro.request.ReservationRequest;
import com.enactor.bus.reservation.system.api.v1.ro.response.ReservationResponse;
import com.enactor.bus.reservation.system.service.exception.InvalidPriceException;
import com.enactor.bus.reservation.system.service.exception.InvalidRouteException;
import com.enactor.bus.reservation.system.service.exception.SeatNotAvailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/api/v1/reserve")
public class ReservationServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            ReservationRequest reservationRequest = parseJson(req, ReservationRequest.class);

            List<String> reservedSeats = bookingService.reserve(reservationRequest);

            int totalPrice = bookingService.calculatePrice(reservationRequest);
            String ticketId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            ReservationResponse reservationResponse = new ReservationResponse(
                    ticketId,
                    reservedSeats,
                    reservationRequest.origin(),
                    reservationRequest.destination(),
                    totalPrice
            );

            writeJson(resp, reservationResponse, HttpServletResponse.SC_CREATED);

        } catch (InvalidRouteException | InvalidPriceException e) {
            writeError(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (SeatNotAvailableException e) {
            writeError(resp, e.getMessage(), HttpServletResponse.SC_CONFLICT);
        } catch (Exception e) {
            writeError(resp, "Internal Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}