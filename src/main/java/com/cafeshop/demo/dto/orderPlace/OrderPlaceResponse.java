package com.cafeshop.demo.dto.orderPlace;

import com.cafeshop.demo.dto.order.OrderResponse;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlaceResponse {
    private Long id;
    private String no;
    private String type;
    private String description;
    private OrderPlaceStatus status;
    private Integer seat;
    private List<OrderResponse> activeOrders;
    private String qrValue;
    private String qrUrl;
}