package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 60)
    private String amount;

    @Column(name = "price", precision = 12, scale = 2, nullable = true)
    private BigDecimal price;

    @Column(length = 255)
    private String note;

    @Column(nullable = true)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
}
