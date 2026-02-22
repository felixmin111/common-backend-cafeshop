package com.cafeshop.demo.repository;


import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderPlaceRepository extends JpaRepository<OrderPlace, Long> {
    List<OrderPlace> findAllByStatusNot(OrderPlaceStatus status);
    boolean existsByNo(String no);

    @Query("""
        select op.id, op.no, op.type, op.description, op.seat, op.status,
               o.id, o.status
        from OrderPlace op
        left join Order o
          on o.orderPlace.id = op.id
         and o.status in :activeStatuses
         and o.id = (
              select max(o2.id)
              from Order o2
              where o2.orderPlace.id = op.id
                and o2.status in :activeStatuses
         )
        where op.status <> com.cafeshop.demo.mode.enums.OrderPlaceStatus.DELETED
        order by op.id asc
    """)
    List<Object[]> findOrderPlacesWithCurrentOrder(@Param("activeStatuses") List<OrderStatus> activeStatuses);


    Long countByStatus(OrderPlaceStatus status);

    @Query("""
        SELECT COUNT(op)
        FROM OrderPlace op
        WHERE op.status <> com.cafeshop.demo.mode.enums.OrderPlaceStatus.DELETED
    """)
    Long countAllActiveTables();
}