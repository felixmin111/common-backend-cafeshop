package com.cafeshop.demo.mode.enums;

public enum OrderStatus {
    PENDING,     // created but not confirmed
    CONFIRMED,   // confirmed by staff/customer
    PREPARING,   // kitchen/bar is working
    READY,       // ready to serve/pickup
    COMPLETED,   // served/paid finished
    CANCELLED    // cancelled
}
