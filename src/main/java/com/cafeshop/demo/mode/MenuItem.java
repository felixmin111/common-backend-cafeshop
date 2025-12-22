package com.cafeshop.demo.mode;

import com.cafeshop.demo.mode.enums.AvailableIn;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menuItems")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String shortDesc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MenuItemStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AvailableIn availableIn;

    @Column(nullable = false)
    private String internalNote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_item_category"))
    private Category category;

}
