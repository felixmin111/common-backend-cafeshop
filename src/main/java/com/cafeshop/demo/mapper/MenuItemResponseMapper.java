package com.cafeshop.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.mode.MenuItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = { MenuItemSizeResponseMapper.class })
public interface MenuItemResponseMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "sizes", source = "sizes")
    @Mapping(target = "ingredients", source = "ingredients")
    @Mapping(target = "images", source = "images")
    MenuItemResponse toDto(MenuItem entity);

    @Mapping(target = "tags", ignore = true)
    MenuItem toEntity(MenuItemResponse dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(MenuItemResponse dto, @MappingTarget MenuItem entity);

}

