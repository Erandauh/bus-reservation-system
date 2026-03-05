package com.enactor.bus.reservation.system;

import com.enactor.bus.reservation.system.model.Bus;
import com.enactor.bus.reservation.system.service.BookingServiceImpl;
import com.enactor.bus.reservation.system.service.BookingService;
import com.enactor.bus.reservation.system.api.v1.AvailabilityHandler;
import com.enactor.bus.reservation.system.api.v1.ReservationHandler;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class BusTicketApplication {
    public static void main(String[] args) throws Exception {
        // Initialize Core Components
        Bus bus = new Bus();
        BookingService bookingService = new BookingServiceImpl(bus);
        Gson gson = new Gson();

        // Create Server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Map Routes to Handlers (The "Controllers")
        server.createContext("/api/v1/availability", new AvailabilityHandler(bookingService, gson));
        server.createContext("/api/v1/reserve", new ReservationHandler(bookingService, gson));

        // Make server run with 10 threads (as basic startup)
        server.setExecutor(Executors.newFixedThreadPool(10));
        System.out.println("Enactor Bus API started on port 8080...");
        server.start();
    }
}