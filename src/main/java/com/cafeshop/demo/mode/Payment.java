package com.cafeshop.demo.mode;

import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many payments can exist for one table/room across time
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_place_id", nullable = false)
    private OrderPlace orderPlace;

    // optional: attach to one order, or leave null if you pay for multiple orders at once
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 30, nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private PaymentStatus status;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    // PromptPay identifier: phone or nationalId or e-wallet id
    @Column(name = "promptpay_id", length = 30)
    private String promptPayId;

    // QR payload text (frontend converts to QR image)
    @Column(name = "qr_payload", length = 1024)
    private String qrPayload;

    // your internal reference for reconciliation
    @Column(name = "reference_no", length = 50, unique = true)
    private String referenceNo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "gateway", length = 30)
    private String gateway; // OMISE, SCB, KBANK

    @Column(name = "gateway_payment_id", length = 100)
    private String gatewayPaymentId;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "raw_callback", columnDefinition = "TEXT")
    private String rawCallback; // store webhook JSON (audit)
}