package com.cafeshop.demo.dto.invoice;

import com.cafeshop.demo.dto.payment.PaymentResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record InvoiceResponse(
        Long id,
        String invoiceNo,
        String customerName,
        BigDecimal subTotal,
        BigDecimal tax,
        BigDecimal deliveryFee,
        BigDecimal grandTotal,
        String status,
        OffsetDateTime appliedAt,
        OffsetDateTime canceledAt,
        OffsetDateTime refundedAt,
        OffsetDateTime createdAt,

        Long orderPlaceId,
        String orderPlaceName,

        Set<InvoiceOrderLineResponse> orders,
        Set<PaymentResponse> payments
) {}

