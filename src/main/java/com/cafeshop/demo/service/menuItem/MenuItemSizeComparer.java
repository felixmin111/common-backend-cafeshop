package com.cafeshop.demo.service.menuItem;

import com.cafeshop.demo.dto.menuItem.MenuItemSizeUpsertRequest;
import com.cafeshop.demo.mode.MenuItemSize;

public final class MenuItemSizeComparer {
    private MenuItemSizeComparer() {}

    public static boolean sameBusinessData(MenuItemSize existing, MenuItemSizeUpsertRequest req) {
        return existing.getSize().getId().equals(req.getSizeId())
                && eq(existing.getSellPrice(), req.getSellPrice())
                && eq(existing.getOriginalPrice(), req.getOriginalPrice())
                && eq(existing.getDescription(), req.getDesc());
    }

    private static boolean eq(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }
}
