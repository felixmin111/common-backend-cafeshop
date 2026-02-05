package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.order.OrderResponse;
import com.cafeshop.demo.mode.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                TagMapper.class,
                IngredientMapper.class,
                MenuItemSizeResponseMapper.class
        }
)
public interface OrderMapper {

    @Mapping(target = "orderPlaceId", source = "orderPlace.id")

    // ----- menuItem basic -----
    @Mapping(target = "menuItem.id", source = "menuItemSize.menuItem.id")
    @Mapping(target = "menuItem.sku", source = "menuItemSize.menuItem.sku")
    @Mapping(target = "menuItem.name", source = "menuItemSize.menuItem.name")
    @Mapping(target = "menuItem.shortDesc", source = "menuItemSize.menuItem.shortDesc")
    @Mapping(target = "menuItem.status", source = "menuItemSize.menuItem.status")
    @Mapping(target = "menuItem.availableIn", source = "menuItemSize.menuItem.availableIn")
    @Mapping(target = "menuItem.internalNote", source = "menuItemSize.menuItem.internalNote")

    // ----- category flattening -----
    @Mapping(target = "menuItem.categoryId", source = "menuItemSize.menuItem.category.id")
    @Mapping(target = "menuItem.categoryName", source = "menuItemSize.menuItem.category.name")

    // ----- collections (require mappers in `uses`) -----
    @Mapping(target = "menuItem.tags", source = "menuItemSize.menuItem.tags")
    @Mapping(target = "menuItem.ingredients", source = "menuItemSize.menuItem.ingredients")
    @Mapping(target = "menuItem.sizes", source = "menuItemSize.menuItem.sizes")

    // ----- size object in OrderResponse -----
    @Mapping(target = "size.id", source = "menuItemSize.size.id")
    @Mapping(target = "size.name", source = "menuItemSize.size.name")
    @Mapping(target = "size.shortName", source = "menuItemSize.size.shortName")
    @Mapping(target = "size.active", source = "menuItemSize.size.active")
    OrderResponse toResponse(Order entity);
    List<OrderResponse> toResponseList(List<Order> entities);
}
