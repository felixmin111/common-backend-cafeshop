package com.cafeshop.demo.mode;

import com.cafeshop.demo.mode.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders") // ✅ use "orders" instead of "order"
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customerName", length = 30)
    private String customerName;

    @Column(name = "qty", nullable = false)
    private Long qty;

    @Column(name = "note", length = 100)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OrderStatus status;

    // Useful snapshot fields (avoid recalculating price later)
    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void addIngredient(OrderIngredient oi) {
        orderIngredients.add(oi);
        oi.setOrder(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderIngredient> orderIngredients = new HashSet<>();

    // FK: menu_item_size_id (you already have this table in your system)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_size_id", nullable = false)
    private MenuItemSize menuItemSize;

    // FK: place_order_id -> order_place.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_order_id", nullable = false)
    private OrderPlace orderPlace;

    @OneToMany(mappedBy = "order")
    private List<InvoiceOrder> invoiceOrders = new ArrayList<>();



    @PrePersist
    public void prePersist() {
        if (status == null) status = OrderStatus.PENDING; // or CONFIRMED
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
    }
}
