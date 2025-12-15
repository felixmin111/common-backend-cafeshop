package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.UserDto;
import com.cafeshop.demo.mode.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "hashPassword", target = "password")
    UserDto toDto(User entity);

    @Mapping(source = "password", target = "hashPassword")
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "password", target = "hashPassword")
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);

}

