package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("""
        select distinct i
        from Invoice i
        left join fetch i.invoiceOrders io
        left join fetch io.order o
        left join fetch o.orderIngredients oi
        left join fetch oi.ingredient ing
        left join fetch i.payments p
        where i.id = :id
    """)
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        select distinct i
        from Invoice i
        left join fetch i.invoiceOrders io
        left join fetch io.order o
        left join fetch o.orderIngredients oi
        left join fetch oi.ingredient ing
        left join fetch i.payments p
        order by i.createdAt desc
    """)
    List<Invoice> findAllWithDetails();
}
