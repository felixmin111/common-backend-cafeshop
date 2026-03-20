package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.vat.VatRequestDto;
import com.cafeshop.demo.dto.vat.VatResponseDto;
import com.cafeshop.demo.mode.Vat;
import com.cafeshop.demo.service.VatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/admin/vats")
@RequiredArgsConstructor
public class VatController {

    private final VatService service;

    @GetMapping
    public List<VatResponseDto> getVats() {
        return service.getVats();
    }

    @GetMapping("{id}")
    public VatResponseDto getVat(@PathVariable Long id) {
        return service.getVatById(id);
    }

    @GetMapping("/active")
    public VatResponseDto getActiveVat() {
        return service.getActiveVat();
    }

    @GetMapping("/default")
    public VatResponseDto getDefaultVat() {
        return service.getDefaultVat();
    }

    @PostMapping
    public VatResponseDto createVat(@RequestBody VatRequestDto dto) {
        return service.createVat(dto);
    }

    @PutMapping("{id}")
    public VatResponseDto updateVat(@PathVariable Long id, @RequestBody VatRequestDto dto) {
        return service.updateVat(id, dto);
    }

    @DeleteMapping("{id}")
    public void deleteVat(@PathVariable Long id) {
        service.deleteVat(id);
    }
}
