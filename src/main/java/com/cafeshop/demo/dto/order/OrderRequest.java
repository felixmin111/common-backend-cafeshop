package com.cafeshop.demo.dto.order;
import com.cafeshop.demo.dto.ingredient.IngredientCreateRequest;
import com.cafeshop.demo.dto.menuitemCreateSize.MenuItemSizeCreateRequest;
import com.cafeshop.demo.dto.orderIngredient.OrderIngredientRequest;
import com.cafeshop.demo.mode.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderRequest {

    private String customerName;

    @NotNull
    private Long qty;

    private String note;

    @NotNull
    private Long menuItemSizeId;

    @NotNull
    private Long orderPlaceId;

    private OrderStatus status;

    private Set<OrderIngredientRequest> ingredients;
}