package com.cafeshop.demo.repository;

import com.cafeshop.demo.dto.dashboard.CategoryOrderCountDto;
import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

    // Today's order count
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.createdAt BETWEEN :start AND :end
        AND o.status <> com.cafeshop.demo.mode.enums.OrderStatus.CANCELLED
    """)
    Long countOrdersBetween(OffsetDateTime start, OffsetDateTime end);

    // Revenue
    @Query(value = """
    SELECT
        DATE(o.created_at) AS date,
        COALESCE(SUM(
            (mis.sell_price - COALESCE(mis.original_price, 0)) * o.qty
        ), 0)::numeric AS profit
    FROM orders o
    JOIN menu_item_sizes mis
        ON o.menu_item_size_id = mis.id
    WHERE o.created_at BETWEEN :start AND :end
      AND o.status = 'COMPLETED'
    GROUP BY DATE(o.created_at)
    ORDER BY DATE(o.created_at)
""", nativeQuery = true)
    List<Object[]> sumProfitBetween(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );


    @Query("""
    SELECT COALESCE(SUM(
        (o.menuItemSize.sellPrice -
         COALESCE(o.menuItemSize.originalPrice, 0)
        ) * o.qty
    ), 0)
    FROM Order o

    WHERE o.createdAt BETWEEN :start AND :end
    AND o.status = com.cafeshop.demo.mode.enums.OrderStatus.COMPLETED
""")
    BigDecimal sumRevenueBetween(OffsetDateTime start, OffsetDateTime end);

    // Popular item
    @Query("""
        SELECT o.menuItemSize.menuItem.name, SUM(o.qty)
        FROM Order o
        WHERE o.createdAt BETWEEN :start AND :end
        GROUP BY o.menuItemSize.menuItem.name
        ORDER BY SUM(o.qty) DESC
    """)
    List<Object[]> findPopularItems(OffsetDateTime start, OffsetDateTime end);

    @Query("""
    SELECT new com.cafeshop.demo.dto.dashboard.CategoryOrderCountDto(
        o.menuItemSize.menuItem.category.name,
        SUM(o.qty)
    )
    FROM Order o
    WHERE o.createdAt BETWEEN :start AND :end
      AND o.status = com.cafeshop.demo.mode.enums.OrderStatus.COMPLETED
    GROUP BY o.menuItemSize.menuItem.category.name
""")
    List<CategoryOrderCountDto> countOrdersByCategoryBetween(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );
}