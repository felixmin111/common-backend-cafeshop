package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.vat.VatRequestDto;
import com.cafeshop.demo.dto.vat.VatResponseDto;
import com.cafeshop.demo.service.VatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vats")
@RequiredArgsConstructor
public class VatController {

    private final VatService service;

    @GetMapping
    public List<VatResponseDto> getAllVats() {
        return service.getVats();
    }

    @GetMapping("{id}")
    public VatResponseDto getVat(@PathVariable Long id) {
        return service.getVatById(id);
    }

    @PostMapping
    public VatResponseDto createVat(@RequestBody @Valid VatRequestDto dto) {
        return service.createVat(dto);
    }

    @PutMapping("{id}")
    public VatResponseDto updateVat(@Valid @RequestBody VatRequestDto dto, @PathVariable Long id) {
        return service.updateVat(id,dto);
    }

    @DeleteMapping("{id}")
    public void deleteVat(@PathVariable Long id) {
        service.deleteVat(id);
    }
}
