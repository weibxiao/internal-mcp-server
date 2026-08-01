package com.example.internalmcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InternalToolsTest {
    private final InternalTools tools = new InternalTools(new InternalStore());

    @Test
    void searchesSeedCustomer() {
        assertEquals("C-1001", tools.searchCustomers("avery", 10).get(0).id());
    }

    @Test
    void createsOnlyPendingReviewOrder() {
        Order order = tools.createOrder("C-1001", "SKU-RED-01", 2, new BigDecimal("12.50"));
        assertEquals("PENDING_REVIEW", order.status());
    }

    @Test
    void rejectsUnknownCustomer() {
        assertThrows(IllegalArgumentException.class,
                () -> tools.createOrder("C-9999", "SKU-RED-01", 1, BigDecimal.ONE));
    }
}
