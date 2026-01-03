package com.cafeshop.demo.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IngredientUpsertRequest {

    private Long id;

    @NotBlank
    private String name;

    private String amount;
    private String note;
}
