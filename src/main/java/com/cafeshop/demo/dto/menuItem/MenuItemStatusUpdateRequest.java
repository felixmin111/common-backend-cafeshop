package com.cafeshop.demo.dto.menuItem;

import com.cafeshop.demo.mode.enums.MenuItemStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemStatusUpdateRequest {
    private MenuItemStatus status;
}
