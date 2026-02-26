package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.InvoiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceOrderRepository extends JpaRepository<InvoiceOrder, Long> {

    List<InvoiceOrder> findByInvoice_Id(Long invoiceId);

    List<InvoiceOrder> findByOrder_Id(Long orderId);

    @Query("""
        select io.order.id, io.invoice.id
        from InvoiceOrder io
        where io.order.id in :orderIds
    """)
    List<Object[]> findInvoiceIdsByOrderIds(@Param("orderIds") List<Long> orderIds);

    @Query("""
        select io.order.id
        from InvoiceOrder io
        where io.invoice.id = :invoiceId
    """)
    List<Long> findOrderIdsByInvoiceId(@Param("invoiceId") Long invoiceId);
}
