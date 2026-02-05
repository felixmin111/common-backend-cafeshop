package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderPlace_IdInAndStatusIn(Collection<Long> placeIds,
                                                 Collection<OrderStatus> statuses);

    @EntityGraph(attributePaths = {
            "menuItemSize",
            "menuItemSize.menuItem",
            "menuItemSize.menuItem.ingredients",
            "menuItemSize.menuItem.tags",
            "menuItemSize.menuItem.sizes",
            "menuItemSize.size",
            "orderPlace",
            "orderIngredients",
            "orderIngredients.ingredient"
    })
    @Query("select o from Order o")
    List<Order> findAllWithDetails();
}