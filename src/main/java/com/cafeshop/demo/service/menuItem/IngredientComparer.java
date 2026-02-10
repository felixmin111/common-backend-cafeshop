package com.cafeshop.demo.service.menuItem;

import com.cafeshop.demo.dto.ingredient.IngredientUpsertRequest;
import com.cafeshop.demo.mode.Ingredient;

public final class IngredientComparer {
    private IngredientComparer() {}

    public static boolean sameBusinessData(Ingredient existing, IngredientUpsertRequest req) {
        return eq(existing.getName(), req.getName())
                && eq(existing.getAmount(), req.getAmount())
                && eq(existing.getPrice(), req.getPrice())
                && eq(existing.getNote(), req.getNote());
    }

    private static boolean eq(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }
}

