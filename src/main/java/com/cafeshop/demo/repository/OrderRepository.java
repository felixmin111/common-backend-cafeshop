package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @Query("""
    select distinct o
    from Order o
        join fetch o.orderPlace op

        join fetch o.menuItemSize mis
        join fetch mis.menuItem mi
        join fetch mi.category
        left join fetch mi.tags
        left join fetch mi.sizes
        join fetch mis.size

        left join fetch o.orderIngredients oi
        left join fetch oi.ingredient
    where o.id = :id
""")
    Optional<Order> findOrderDetailsById(@Param("id") Long id);
}