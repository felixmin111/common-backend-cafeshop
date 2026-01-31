package com.cafeshop.demo.mapper;


import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mode.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "orderPlaceId", source = "orderPlace.id")
    @Mapping(target = "orderId", source = "order.id")
    PaymentResponse toResponse(Payment entity);
}

