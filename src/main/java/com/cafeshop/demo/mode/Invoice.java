package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invoices")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", length = 50, unique = true)
    private String invoiceNo;

    @Column(name = "customer_name", length = 40)
    private String customerName;

    @Column(name = "sub_total", precision = 19, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "grand_total", precision = 19, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "status", length = 50)
    private String status; // or enum

    @Column(name = "delivery_fee", precision = 19, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "tax", precision = 19, scale = 2)
    private BigDecimal tax;

    @Column(name = "applied_at")
    private OffsetDateTime appliedAt;

    @Column(name = "canceled_at")
    private OffsetDateTime canceledAt;

    @Column(name = "refunded_at")
    private OffsetDateTime refundedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // FK: invoice.order_place_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_place_id")
    private OrderPlace orderPlace;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InvoiceOrder> invoiceOrders = new HashSet<>();


    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Payment> payments = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (appliedAt == null) appliedAt = OffsetDateTime.now();
        if (subTotal == null) subTotal = BigDecimal.ZERO;
        if (tax == null) tax = BigDecimal.ZERO;
        if (deliveryFee == null) deliveryFee = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
        if (status == null) status = "PENDING";
    }
}
