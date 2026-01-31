package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderPlace_IdInAndStatusIn(Collection<Long> placeIds,
                                                 Collection<OrderStatus> statuses);
}