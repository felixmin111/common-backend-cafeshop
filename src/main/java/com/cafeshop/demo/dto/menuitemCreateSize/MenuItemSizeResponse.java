package com.cafeshop.demo.dto.menuitemCreateSize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemSizeResponse {
    private long id;
    private Long menu_item_id;
    private Long size_id;
    private String name;
    private String shortName;
    private Double originalPrice;
    private Double sellPrice;
    private String description;
}
