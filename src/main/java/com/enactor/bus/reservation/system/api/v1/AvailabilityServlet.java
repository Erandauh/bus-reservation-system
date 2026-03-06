package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.v1.ro.request.AvailabilityRequest;
import com.enactor.bus.reservation.system.api.v1.ro.response.AvailabilityResponse;
import com.enactor.bus.reservation.system.service.BookingService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/availability")
public class AvailabilityServlet extends HttpServlet {
    private BookingService bookingService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.bookingService = (BookingService) getServletContext().getAttribute("bookingService");
        this.gson = (Gson) getServletContext().getAttribute("gson");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String origin = req.getParameter("origin");
            String destination = req.getParameter("destination");
            String passengersParam = req.getParameter("passengers");
            int passengers = Integer.parseInt(passengersParam != null ? passengersParam : "1");

            if (origin == null || destination == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(java.util.Map.of("error", "Missing origin or destination")));
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

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(availabilityResponse));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(java.util.Map.of("error", "Invalid passenger count")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(java.util.Map.of("error", "Internal server error: " + e.getMessage())));
        }
    }
}