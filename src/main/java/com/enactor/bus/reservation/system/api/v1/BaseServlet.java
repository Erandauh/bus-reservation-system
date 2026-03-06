package com.enactor.bus.reservation.system.api.v1;

import com.enactor.bus.reservation.system.service.BookingService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class BaseServlet extends HttpServlet {

    protected BookingService bookingService;
    protected Gson gson;

    @Override
    public void init() throws ServletException {
        this.bookingService = (BookingService) getServletContext().getAttribute("bookingService");
        this.gson = (Gson) getServletContext().getAttribute("gson");
    }

    protected void writeJson(HttpServletResponse resp, Object data, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(data));
    }

    protected void writeError(HttpServletResponse resp, String message, int status) throws IOException {
        writeJson(resp, Map.of("error", message), status);
    }

    protected <T> T parseJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            return gson.fromJson(reader, clazz);
        }
    }
}
