package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "menu_item_sizes",
        uniqueConstraints = @UniqueConstraint(name="uk_menu_item_size", columnNames={"menu_item_id","size_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_item_sizes_menu_item"))
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_item_sizes_size"))
    private Size size;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "sell_price", nullable = false)
    private Double sellPrice;

    @Column(name = "description", length = 300)
    private String description;
}

