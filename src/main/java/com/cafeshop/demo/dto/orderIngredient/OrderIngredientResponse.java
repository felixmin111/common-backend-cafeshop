package com.cafeshop.demo.dto.orderIngredient;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public record OrderIngredientResponse(
        Long id,
        Long ingredientId,
        String ingredientName,
        BigDecimal price,
        BigDecimal qty,
        String note
) {}

