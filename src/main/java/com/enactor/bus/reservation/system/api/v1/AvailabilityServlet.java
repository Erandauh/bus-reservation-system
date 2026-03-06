package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.v1.ro.request.AvailabilityRequest;
import com.enactor.bus.reservation.system.api.v1.ro.response.AvailabilityResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/availability")
public class AvailabilityServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            String origin = req.getParameter("origin");
            String destination = req.getParameter("destination");
            String passengersParam = req.getParameter("passengers");
            int passengers = Integer.parseInt(passengersParam != null ? passengersParam : "1");

            if (origin == null || destination == null) {
                writeError(resp, "Missing origin or destination", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            AvailabilityRequest availabilityRequest = new AvailabilityRequest(origin, destination, passengers);
            List<String> seats = bookingService.checkAvailability(availabilityRequest);
            int price = bookingService.calculatePrice(availabilityRequest);

            AvailabilityResponse availabilityResponse = new AvailabilityResponse(
                    seats,
                    price,
                    origin,
                    destination
            );

            writeJson(resp, availabilityResponse, HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            writeError(resp, "Invalid passenger count format", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            writeError(resp, "Internal server error: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}