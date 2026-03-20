package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.invoice.CustomerOrderResponse;
import com.cafeshop.demo.dto.order.OrderResponse;
import com.cafeshop.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrdersController {
    private final OrderService service;


    @GetMapping("/{invoiceId}")
    public List<CustomerOrderResponse> getOrdersByInvoice(@PathVariable Long invoiceId) {
        return service.getByInvoiceId(invoiceId);
    }
}