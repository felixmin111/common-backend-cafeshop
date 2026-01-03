package com.cafeshop.demo.dto.ingredient;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientResponse {
    private Long id;
    private String name;
    private String amount;
    private String note;
}
