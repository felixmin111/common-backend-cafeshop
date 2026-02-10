package com.cafeshop.demo.dto.menuItem;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class MenuItemSizeUpsertRequest {
    private Long id;
    private Long sizeId;
    private Double originalPrice;
    private Double sellPrice;
    private String desc;
}
