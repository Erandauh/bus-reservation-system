package com.enactor.bus.reservation.system.config;

import com.enactor.bus.reservation.system.service.BookingServiceImpl;
import com.enactor.bus.reservation.system.model.Bus;
import com.enactor.bus.reservation.system.service.StandardPricing;
import com.google.gson.Gson;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        BookingServiceImpl bookingService = new BookingServiceImpl(new Bus(), new StandardPricing());
        Gson gson = new Gson();

        sce.getServletContext().setAttribute("bookingService", bookingService);
        sce.getServletContext().setAttribute("gson", gson);
    }
}