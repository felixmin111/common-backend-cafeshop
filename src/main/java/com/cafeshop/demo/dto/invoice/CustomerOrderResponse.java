package com.cafeshop.demo.dto.invoice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class CustomerOrderResponse {

    private Long orderId;
    private Long invoiceId;

    private String menuItemName;
    private String sizeName;

    private Long qty;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String tableNo;
    private String orderType;

    private String status;

    private OffsetDateTime createdAt;
}