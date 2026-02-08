package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "invoice_order",
        uniqueConstraints = @UniqueConstraint(name="uk_invoice_order", columnNames={"invoice_id","order_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InvoiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name="menu_item_name", length = 50)
    private String menuItemName;

    @Column(name="size_name", length = 30)
    private String sizeName;

    @Column(nullable = false)
    private Long qty;

    @Column(name="unit_price", precision=19, scale=2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name="line_total", precision=19, scale=2, nullable = false)
    private BigDecimal lineTotal;

    @Column(length = 50)
    private String note;

    @Column(name="created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
