package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderPlace_Id(Long orderPlaceId);
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
}
