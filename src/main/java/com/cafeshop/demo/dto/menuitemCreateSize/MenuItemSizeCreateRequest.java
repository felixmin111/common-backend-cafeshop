package com.cafeshop.demo.dto.menuitemCreateSize;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemSizeCreateRequest {
    private Long sizeId;
    private Double originalPrice;
    private Double sellPrice;
    private String desc;
    @Column(nullable = false)
    private boolean active = true;
}

