package com.cafeshop.demo.dto.invoice;

import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mode.enums.TaxType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record InvoiceResponse(
        Long id,
        String invoiceNo,
        String customerName,
        BigDecimal subTotal,
        BigDecimal tax,
        BigDecimal vatRate,
        String vatName,
        TaxType vatType,
        BigDecimal deliveryFee,
        BigDecimal grandTotal,
        String status,
        OffsetDateTime appliedAt,
        OffsetDateTime canceledAt,
        OffsetDateTime refundedAt,
        OffsetDateTime createdAt,

        Long orderPlaceId,
        String orderPlaceName,
        String no,
        String type,

        Set<InvoiceOrderLineResponse> orders,
        Set<PaymentResponse> payments
) {}

