package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.UserDto;
import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MenuItemCreateRequestMapper {
    MenuItemCreateRequest toDto(MenuItem entity);
    MenuItem toEntity(MenuItemCreateRequest dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(MenuItemCreateRequest dto, @MappingTarget MenuItem entity);

}

