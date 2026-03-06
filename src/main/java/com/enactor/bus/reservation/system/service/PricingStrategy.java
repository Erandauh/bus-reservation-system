package com.enactor.bus.reservation.system.service;

import java.util.List;

/**
 * This gives capability to inject different pricing strategies
 * e.g. special prices for between specific destinations
 */
public interface PricingStrategy {
    int calculate(List<String> route, String from, String to, int count);
}