package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderPlace_Id(Long orderPlaceId);
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
    @EntityGraph(attributePaths = {
            "invoice",
            "invoice.invoiceOrders",
            "invoice.invoiceOrders.order",
            "invoice.invoiceOrders.order.orderIngredients",
            "invoice.invoiceOrders.order.orderIngredients.ingredient"
    })
    Optional<Payment> findWithDetailsById( Long id);

    List<Payment> findByInvoiceId(Long invoiceId);
}
