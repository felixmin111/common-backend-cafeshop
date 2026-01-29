package com.cafeshop.demo.dto.order;
import com.cafeshop.demo.mode.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderRequest {

    private String guestId;

    @NotNull
    private Long qty;

    private String note;

    @NotNull
    private Long menuItemSizeId;

    @NotNull
    private Long orderPlaceId;

    private OrderStatus status;
}