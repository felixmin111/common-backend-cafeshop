package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.menuitemCreateSize.MenuItemSizeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.cafeshop.demo.mode.MenuItemSize;

@Mapper(componentModel = "spring")
public interface MenuItemSizeResponseMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "menu_item_id", source = "menuItem.id")
    @Mapping(target = "size_id", source = "size.id")
    @Mapping(target = "name", source = "size.name")
    @Mapping(target = "shortName", source = "size.shortName")

    MenuItemSizeResponse toDto(MenuItemSize entity);
}
