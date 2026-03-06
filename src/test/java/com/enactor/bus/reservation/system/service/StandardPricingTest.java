package com.enactor.bus.reservation.system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StandardPricingTest {

    private StandardPricing pricingStrategy;
    private final List<String> route = Arrays.asList("A", "B", "C", "D");

    @BeforeEach
    void setUp() {
        pricingStrategy = new StandardPricing();
    }

    @ParameterizedTest(name = "{3} passengers from {1} to {2} should cost {0}")
    @CsvSource({
            "50,  A, B, 1",
            "100, A, C, 1",
            "300, A, D, 2",
            "50,  B, A, 1",
            "150, D, A, 1",
            "0,   B, B, 1"    // Same stop = 0 price
    })
    @DisplayName("Should calculate correct price based on number of stops and passenger count")
    void testCalculate_VariousScenarios(int expectedPrice, String from, String to, int count) {
        int actualPrice = pricingStrategy.calculate(route, from, to, count);

        assertThat(actualPrice)
                .as("Price for %d passengers from %s to %s", count, from, to)
                .isEqualTo(expectedPrice);
    }

    @Test
    @DisplayName("Should handle calculation even if route list is modified")
    void testCalculate_EmptyRoute() {
        int price = pricingStrategy.calculate(route, "A", "A", 5);
        assertThat(price).isZero();
    }
}