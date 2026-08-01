package com.example.internalmcp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class InternalStore {
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public InternalStore() {
        customers.put("C-1001", new Customer("C-1001", "Avery Chen", "avery.chen@example.test", "ACTIVE"));
        customers.put("C-1002", new Customer("C-1002", "Jordan Patel", "jordan.patel@example.test", "ACTIVE"));
    }

    public Optional<Customer> customer(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    public List<Customer> searchCustomers(String query, int limit) {
        String normalized = query.toLowerCase(Locale.ROOT).trim();
        return customers.values().stream()
                .filter(customer -> customer.id().toLowerCase(Locale.ROOT).contains(normalized)
                        || customer.name().toLowerCase(Locale.ROOT).contains(normalized)
                        || customer.email().toLowerCase(Locale.ROOT).contains(normalized))
                .limit(limit)
                .toList();
    }

    public Order createOrder(String customerId, String sku, int quantity, BigDecimal unitPrice) {
        Order order = new Order("O-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                customerId, sku, quantity, unitPrice, "PENDING_REVIEW", Instant.now());
        orders.put(order.id(), order);
        return order;
    }

    public Collection<Order> orders() {
        return List.copyOf(orders.values());
    }
}
