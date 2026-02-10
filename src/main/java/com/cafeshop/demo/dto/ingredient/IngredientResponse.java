package com.cafeshop.demo.dto.ingredient;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class IngredientResponse {
    private Long id;
    private String name;
    private String amount;
    private BigDecimal price;
    private String note;
    private boolean active;

}
