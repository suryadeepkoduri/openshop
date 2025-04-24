package com.suryadeep.openshop.entity.enums;

import java.util.Arrays;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED;

    public static boolean contains(String value) {
        return Arrays.stream(values())
                .anyMatch(status -> status.name().equalsIgnoreCase(value));
    }
}