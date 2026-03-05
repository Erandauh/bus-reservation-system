package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.api.BaseHandler;
import com.enactor.bus.reservation.system.service.BookingService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AvailabilityHandler extends BaseHandler implements HttpHandler {
    private final BookingService bookingService;
    private final Gson gson;

    public AvailabilityHandler(BookingService bookingService, Gson gson) {
        this.bookingService = bookingService;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, "{\"error\":\"Method not allowed. " +
                    "Use GET for availability.\"}", 405);
            return;
        }

        // Parse query parameters from URI: /api/v1/availability?origin=A&destination=B&passengers=2
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQueryParams(query);

        try {
            String origin = params.get("origin");
            String destination = params.get("destination");
            int count = Integer.parseInt(params.getOrDefault("passengers", "1"));

            if (origin == null || destination == null) {
                sendResponse(exchange, "{\"error\":\"Missing required parameters: origin and destination\"}", 400);
                return;
            }

            List<String> seats = bookingService.checkAvailability(origin, destination);
            int price = bookingService.calculatePrice(origin, destination, count);

            String response = gson.toJson(Map.of(
                    "available_seats", seats,
                    "total_price", price
            ));

            sendResponse(exchange, response, 200);

        } catch (NumberFormatException e) {
            sendResponse(exchange, "{\"error\":\"Invalid passenger count\"}", 400);
        }
    }
}