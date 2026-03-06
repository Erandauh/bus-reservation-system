package com.enactor.bus.reservation.system.service;

import java.util.List;

public class StandardPricing implements PricingStrategy {

    private static final int PRICE_PER_STOP = 50;

    @Override
    public int calculate(List<String> route, String from, String to, int count) {
        int stops = Math.abs(route.indexOf(to) - route.indexOf(from));
        return stops * PRICE_PER_STOP * count;
    }
}
