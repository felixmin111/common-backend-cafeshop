package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;

    private Long orderPlaceId;

    // Payment fields
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;

    private String gateway;
    private String gatewayPaymentId;
    private String referenceNo;

    private String qrPayload;
    private String qrImageUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime paidAt;

    // ✅ Invoice summary
    private Long invoiceId;
    private String invoiceNo;
    private String customerName;
    private String invoiceStatus;

    private BigDecimal subTotal;
    private BigDecimal tax;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;

    private OffsetDateTime appliedAt;

    // ✅ Items in this payment/invoice
    private java.util.List<PaymentItemResponse> items;
}

