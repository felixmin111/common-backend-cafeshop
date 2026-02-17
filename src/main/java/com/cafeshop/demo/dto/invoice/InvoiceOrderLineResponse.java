package com.cafeshop.demo.dto.invoice;

import com.cafeshop.demo.dto.ingredient.IngredientResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record InvoiceOrderLineResponse(
        Long id,
        Long orderId,
        String menuItemName,
        String sizeName,
        Long qty,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String note,
        OffsetDateTime createdAt,
        Set<IngredientResponse> ingredientResponses
) {}

