package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.ingredient.IngredientCreateRequest;
import com.cafeshop.demo.dto.ingredient.IngredientResponse;
import com.cafeshop.demo.dto.ingredient.IngredientUpsertRequest;
import com.cafeshop.demo.mode.Ingredient;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    @Mapping(target = "menuItem", ignore = true)
    Ingredient toEntity(IngredientCreateRequest dto);

    IngredientResponse toDto(Ingredient entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "menuItem", ignore = true)
    void updateEntityFromDto(IngredientUpsertRequest dto, @MappingTarget Ingredient entity);
}
