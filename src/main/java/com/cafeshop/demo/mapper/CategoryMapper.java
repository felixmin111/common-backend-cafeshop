package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.category.CategoryDto;
import com.cafeshop.demo.dto.category.CategoryRequestDto;
import com.cafeshop.demo.mode.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "menuItems", ignore = true)
    Category toEntity(CategoryRequestDto dto);

    @Mapping(target = "menuItemCount",
            expression = "java(category.getMenuItems() == null ? 0L : category.getMenuItems().size())")
    CategoryDto toDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "menuItems", ignore = true)
    void updateEntity(CategoryRequestDto dto, @MappingTarget Category entity);
}
