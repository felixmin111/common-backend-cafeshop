package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.order.OrderResponse;
import com.cafeshop.demo.mode.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "menuItemSizeId", source = "menuItemSize.id")
    @Mapping(target = "orderPlaceId", source = "orderPlace.id")
    OrderResponse toResponse(Order entity);
    List<OrderResponse> toResponseList(List<Order> entities);
}
