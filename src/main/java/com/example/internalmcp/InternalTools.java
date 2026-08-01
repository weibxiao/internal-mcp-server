package com.example.internalmcp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class InternalTools {
    private final InternalStore store;

    public InternalTools(InternalStore store) {
        this.store = store;
    }

    @McpTool(name = "search_customers", description = "Search the approved customer directory by customer ID, name, or email. Returns only a minimal safe profile.")
    public List<Customer> searchCustomers(
            @McpToolParam(description = "A non-empty customer ID, name, or email fragment", required = true) String query,
            @McpToolParam(description = "Maximum results, from 1 to 25", required = false) Integer limit) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }
        int safeLimit = limit == null ? 10 : limit;
        if (safeLimit < 1 || safeLimit > 25) {
            throw new IllegalArgumentException("limit must be between 1 and 25");
        }
        return store.searchCustomers(query, safeLimit);
    }

    @McpTool(name = "get_customer", description = "Get an approved minimal profile for one customer ID.")
    public Customer getCustomer(
            @McpToolParam(description = "Customer ID, for example C-1001", required = true) String customerId) {
        return store.customer(customerId)
                .orElseThrow(() -> new IllegalArgumentException("No customer found for ID " + customerId));
    }

    @McpTool(name = "create_order", description = "Create a pending-review order for an existing customer. This starter never submits or charges an order.")
    public Order createOrder(
            @McpToolParam(description = "Existing customer ID", required = true) String customerId,
            @McpToolParam(description = "Internal SKU", required = true) String sku,
            @McpToolParam(description = "Quantity, from 1 to 100", required = true) int quantity,
            @McpToolParam(description = "Unit price in the configured currency, greater than zero", required = true) BigDecimal unitPrice) {
        if (store.customer(customerId).isEmpty()) {
            throw new IllegalArgumentException("Unknown customer ID " + customerId);
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity < 1 || quantity > 100) {
            throw new IllegalArgumentException("quantity must be between 1 and 100");
        }
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("unitPrice must be greater than zero");
        }
        return store.createOrder(customerId, sku.trim(), quantity, unitPrice);
    }

    @McpTool(name = "run_health_check", description = "Run a non-invasive service health check. It does not reveal secrets or infrastructure addresses.")
    public Map<String, Object> runHealthCheck() {
        return Map.of("status", "UP", "checkedAt", Instant.now().toString(), "customerStore", "UP", "orderStore", "UP");
    }

    @McpResource(uri = "internal://service-info", name = "Internal MCP service information", description = "Safe operational information and supported tool policy.")
    public String serviceInfo() {
        return """
                {"service":"internal-mcp-server","version":"0.1.0","dataMode":"in-memory demo data","orderPolicy":"Orders are created only with PENDING_REVIEW status; no payment or fulfillment is performed.","tools":["search_customers","get_customer","create_order","run_health_check"]}
                """;
    }
}
