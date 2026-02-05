package com.cafeshop.demo.dto.orderIngredient;

import java.math.BigDecimal;

public record OrderIngredientResponse(
        Long id,
        Long ingredientId,
        String ingredientName,
        BigDecimal qty,
        String note
) {}

