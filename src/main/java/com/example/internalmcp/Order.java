package com.example.internalmcp;

import java.math.BigDecimal;
import java.time.Instant;

public record Order(String id, String customerId, String sku, int quantity,
                    BigDecimal unitPrice, String status, Instant createdAt) {
}
