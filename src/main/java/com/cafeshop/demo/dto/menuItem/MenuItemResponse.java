package com.cafeshop.demo.dto.menuItem;

import com.cafeshop.demo.mode.enums.AvailableIn;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuItemResponse {
    private Long id;
    private String sku;
    private String name;
    private String shortDesc;
    private MenuItemStatus status;
    private AvailableIn availableIn;
    private String internalNote;

    private Long categoryId;
    private String categoryName;
}