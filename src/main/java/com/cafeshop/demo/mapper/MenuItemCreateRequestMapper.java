package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.UserDto;
import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.dto.menuitemCreateSize.MenuItemSizeResponse;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.User;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface MenuItemCreateRequestMapper {
    MenuItemCreateRequest toDto(MenuItem entity);

    @Mapping(target = "sizes", ignore = true)
    MenuItem toEntity(MenuItemCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "sizes", ignore = true)
    void updateEntityFromDto(MenuItemCreateRequest dto, @MappingTarget MenuItem entity);
}

