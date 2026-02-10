package com.cafeshop.demo.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class IngredientUpsertRequest {

    private Long id;

    @NotBlank
    private String name;
    private BigDecimal price;
    private String amount;
    private String note;
}
