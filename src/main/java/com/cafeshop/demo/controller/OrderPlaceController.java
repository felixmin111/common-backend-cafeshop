package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.orderPlace.OrderPlaceRequest;
import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.service.OrderPlaceQueryService;
import com.cafeshop.demo.service.OrderPlaceService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/order-places")
@RequiredArgsConstructor
public class OrderPlaceController {
    private final OrderPlaceQueryService orderPlaceQueryService;
    private final OrderPlaceService service;

    @PostMapping
    public ResponseEntity<OrderPlaceResponse> create(@Valid @RequestBody OrderPlaceRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderPlaceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderPlaceResponse>> getAll() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderPlaceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderPlaceRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/with-current-order")
    public ResponseEntity<List<OrderPlaceResponse>> getAllWithCurrentOrder() {
        return ResponseEntity.ok(orderPlaceQueryService.getOrderPlacesWithActiveOrders());
    }

    @GetMapping(value = "/api/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qr(@RequestParam String text) throws Exception {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 320, 320, hints);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);

        return ResponseEntity.ok(out.toByteArray());
    }
}

