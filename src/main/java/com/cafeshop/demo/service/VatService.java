package com.cafeshop.demo.service;


import com.cafeshop.demo.dto.vat.VatRequestDto;
import com.cafeshop.demo.dto.vat.VatResponseDto;
import com.cafeshop.demo.mode.Vat;
import com.cafeshop.demo.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VatService {

    private final VatRepository vatRepository;

    public List<VatResponseDto> getVats() {
        return vatRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public VatResponseDto getVatById(Long id) {
        return vatRepository.findById(id).map(this::mapToDto).orElse(null);
    }

    public VatResponseDto createVat(VatRequestDto vatRequestDto) {
        Vat vat = new Vat();
        if (Boolean.TRUE.equals(vatRequestDto.getIsDefault())) {
            vatRepository.clearDefaults();
        }
        vat.setVatCode(vatRequestDto.getVatCode());
        vat.setVatName(vatRequestDto.getVatName());
        vat.setVatRate(vatRequestDto.getVatRate());
        vat.setTaxType(vatRequestDto.getTaxType());
        vat.setDefault(vatRequestDto.getIsDefault());
        vat.setActive(vatRequestDto.getIsActive());

        Vat saved = vatRepository.save(vat);
        return mapToDto(saved);
    }

    public VatResponseDto updateVat(Long id,VatRequestDto vatRequestDto) {
            Vat vat = vatRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("VAT not found"));
        if (Boolean.TRUE.equals(vatRequestDto.getIsDefault())) {
            vatRepository.clearDefaults();
        }
        vat.setVatCode(vatRequestDto.getVatCode());
        vat.setVatName(vatRequestDto.getVatName());
        vat.setVatRate(vatRequestDto.getVatRate());
        vat.setTaxType(vatRequestDto.getTaxType());
        vat.setDefault(vatRequestDto.getIsDefault());
        vat.setActive(vatRequestDto.getIsActive());

        Vat updated = vatRepository.save(vat);
        if (!vatRepository.existsByIsDefaultTrue()) {
            throw new RuntimeException("At least one VAT must be default");
        }
        return mapToDto(updated);
    }

    public void deleteVat(Long id) {
        vatRepository.deleteById(id);
    }

    private VatResponseDto mapToDto(Vat v) {
        VatResponseDto dto = new VatResponseDto();
        dto.setId(v.getId());
        dto.setVatCode(v.getVatCode());
        dto.setVatName(v.getVatName());
        dto.setTaxType(v.getTaxType());
        dto.setVatRate(v.getVatRate());
        dto.setDefault(v.isDefault());
        dto.setActive(v.isActive());
        dto.setCreatedAt(v.getCreatedAt());
        dto.setUpdatedAt(v.getUpdatedAt());
        return dto;
    }

    public Vat getDefaultVat() {
        return vatRepository.findByIsDefaultTrueAndIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No default VAT found"));
    }
}
