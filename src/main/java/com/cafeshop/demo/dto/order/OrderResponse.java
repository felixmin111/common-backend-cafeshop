package com.cafeshop.demo.dto.order;

import com.cafeshop.demo.dto.ingredient.IngredientResponse;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.dto.menuitemCreateSize.MenuItemSizeResponse;
import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.dto.size.SizeResponse;
import com.cafeshop.demo.mode.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String guestId;
    private Long qty;
    private String note;

    private Long menuItemSizeId;
    private Long orderPlaceId;

    private MenuItemResponse menuItem;
    private SizeResponse size;
    private OrderPlaceResponse orderPlace;
    private MenuItemSizeResponse menuItemSize;
    private IngredientResponse ingredient;

    private OrderStatus status;

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
