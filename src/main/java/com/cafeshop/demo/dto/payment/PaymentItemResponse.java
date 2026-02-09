package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.dto.ingredient.IngredientResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItemResponse {

    private Long orderId;          // from invoice_order.order_id
    private Long menuItemSizeId;   // from order.menu_item_size_id (optional)

    private String menuItemName;   // snapshot
    private String sizeName;       // snapshot

    private Long qty;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String note;

    private Set<PaymentIngredientResponse> ingredients;
}