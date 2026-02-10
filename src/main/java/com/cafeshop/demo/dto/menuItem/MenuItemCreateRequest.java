package com.cafeshop.demo.dto.menuItem;
import com.cafeshop.demo.dto.ingredient.IngredientUpsertRequest;
import com.cafeshop.demo.mode.enums.AvailableIn;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import jakarta.persistence.Column;
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

    @Column(nullable = false)
    private Boolean active = true;


    @NotNull(message = "categoryId is required")
    private Long categoryId;

    @NotNull(message = "tagIds is required")
    @NotEmpty(message = "tagIds cannot be empty")
    private Set<Long> tagIds;

    private Set<MenuItemSizeUpsertRequest> sizes;
    private Set<IngredientUpsertRequest> ingredients;


}