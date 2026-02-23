package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.menuItemImage.MenuItemImageDto;
import com.cafeshop.demo.mode.MenuItemImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemImageMapper {

    MenuItemImageDto toDto(MenuItemImage entity);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "s3Key", ignore = true)     // optional: keep if s3Key is server-controlled
    @Mapping(target = "active", ignore = true)    // optional: server-controlled
    @Mapping(target = "createdAt", ignore = true) // optional: server-controlled (@PrePersist)
    MenuItemImage toEntity(MenuItemImageDto dto);
}