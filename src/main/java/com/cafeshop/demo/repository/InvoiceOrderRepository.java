package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.InvoiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceOrderRepository extends JpaRepository<InvoiceOrder, Long> {

    List<InvoiceOrder> findByInvoice_Id(Long invoiceId);

    List<InvoiceOrder> findByOrder_Id(Long orderId);
}
