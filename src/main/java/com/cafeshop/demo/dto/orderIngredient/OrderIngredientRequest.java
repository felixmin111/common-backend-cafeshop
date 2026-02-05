package com.cafeshop.demo.dto.orderIngredient;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class OrderIngredientRequest {
    private Long ingredientId;
    private BigDecimal qty;
    private String note;
}
