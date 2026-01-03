package com.cafeshop.demo.dto.menuItem;

import com.cafeshop.demo.dto.ingredient.IngredientCreateRequest;
import com.cafeshop.demo.dto.ingredient.IngredientUpsertRequest;
import com.cafeshop.demo.dto.menuitemCreateSize.MenuItemSizeCreateRequest;
import com.cafeshop.demo.mode.enums.AvailableIn;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MenuItemCreateRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    @NotBlank
    private String shortDesc;

    @NotNull
    private MenuItemStatus status;

    @NotNull
    private AvailableIn availableIn;

    @NotBlank
    private String internalNote;

    @NotNull(message = "categoryId is required")
    private Long categoryId;

    @NotNull(message = "tagIds is required")
    @NotEmpty(message = "tagIds cannot be empty")
    private Set<Long> tagIds;

    private Set<MenuItemSizeCreateRequest> sizes;
    private Set<IngredientCreateRequest> ingredients;


}