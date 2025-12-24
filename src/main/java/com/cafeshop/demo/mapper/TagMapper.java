package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.tag.TagRequest;
import com.cafeshop.demo.dto.tag.TagResponse;
import com.cafeshop.demo.mode.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Tag toEntity(TagRequest req);

    @Mapping(target = "products", ignore = true)
    void update(@MappingTarget Tag tag, TagRequest req);
}

