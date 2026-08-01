package com.example.internalmcp;

/** Deliberately minimal, non-sensitive view of a customer. */
public record Customer(String id, String name, String email, String status) {
}
