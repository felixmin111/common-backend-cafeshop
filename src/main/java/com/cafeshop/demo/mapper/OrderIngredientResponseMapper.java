package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.orderIngredient.OrderIngredientResponse;
import com.cafeshop.demo.mode.OrderIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderIngredientResponseMapper {

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "ingredientName", source = "ingredient.name")
    @Mapping(target = "price", source = "ingredient.price")
    OrderIngredientResponse toResponse(OrderIngredient entity);

    List<OrderIngredientResponse> toResponseList(List<OrderIngredient> entities);
}