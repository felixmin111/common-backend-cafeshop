package com.cafeshop.demo.dto.menuItem;

import com.cafeshop.demo.mode.enums.AvailableIn;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
}