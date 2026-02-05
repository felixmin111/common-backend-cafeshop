package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.orderPlace.OrderPlaceRequest;
import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.mode.OrderPlace;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface OrderPlaceMapper {
    @Mapping(target = "qrValue", ignore = true)
    @Mapping(target = "qrUrl", ignore = true)
    OrderPlaceResponse toResponse(OrderPlace entity);

    // request -> entity (for create)
    @Mapping(target = "id", ignore = true)
    OrderPlace toEntity(OrderPlaceRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seat", source = "seat")
    void updateEntity(@MappingTarget OrderPlace entity, OrderPlaceRequest request);

}
