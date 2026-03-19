package com.cafeshop.demo.dto.vat;

import com.cafeshop.demo.mode.enums.TaxType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class VatResponseDto {
    private Long id;

    private String vatCode;
    private String vatName;

    private TaxType taxType;
    private BigDecimal vatRate;

    private boolean isActive;
    private boolean isDefault;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
