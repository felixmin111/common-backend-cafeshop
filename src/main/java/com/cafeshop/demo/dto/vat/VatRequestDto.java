package com.cafeshop.demo.dto.vat;

import com.cafeshop.demo.mode.enums.TaxType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VatRequestDto {

    @NotBlank
    private String vatCode;

    @NotBlank
    private String vatName;

    @NotNull
    private TaxType taxType;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal vatRate;

    private Boolean isActive;
    private Boolean isDefault;
}