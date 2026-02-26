package com.cafeshop.demo.dto.payment;

public record PaymentUpdateEvent(
        Long invoiceId,
        Long paymentId,
        String paymentStatus,
        String paymentType,
        java.util.List<Long> orderIds
) {}