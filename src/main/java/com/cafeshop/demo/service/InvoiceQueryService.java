package com.cafeshop.demo.service;
// package com.cafeshop.demo.service.invoice;

import com.cafeshop.demo.dto.invoice.InvoiceResponse;
import com.cafeshop.demo.mapper.InvoiceResponseMapper;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.repository.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceQueryService {

    private final InvoiceRepository invoiceRepo;
    private final InvoiceResponseMapper mapper;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAll() {
        return invoiceRepo.findAllWithDetails()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        Invoice invoice = invoiceRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + id));
        return mapper.toResponse(invoice);
    }


}
