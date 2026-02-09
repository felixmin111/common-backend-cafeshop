package com.cafeshop.demo.dto.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIngredientResponse {
    private Long ingredientId;
    private String name;
    private BigDecimal qty;
    private String note;
    private String price;
}