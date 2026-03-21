package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.orderPlace.OrderPlaceRequest;
import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.mapper.OrderPlaceMapper;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderPlaceService {

    private final OrderPlaceRepository repo;
    private final OrderPlaceMapper mapper;

    public OrderPlaceResponse create(OrderPlaceRequest req) {
        OrderPlace entity = mapper.toEntity(req);
        if (entity.getStatus() == null) {
            entity.setStatus(OrderPlaceStatus.ACTIVE);
        }

        return mapper.toResponse(repo.save(entity));
    }

    @Transactional(readOnly = true)
    public OrderPlaceResponse getById(Long id) {
        OrderPlace entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + id));
        return mapper.toResponse(entity);
    }
    @Value("${app.cors.allowed-origins:}")
    private String frontendBaseUrl;

    @Transactional(readOnly = true)
    public List<OrderPlaceResponse> getAllActive() {
        return repo.findAllByStatusNot(OrderPlaceStatus.INACTIVE)
                .stream()
                .map(entity -> {
                    OrderPlaceResponse base = mapper.toResponse(entity);

                    String type = entity.getType();
                    String no = entity.getNo();

                    String qrValue = type + ":" + no;

                    String qrUrl = frontendBaseUrl
                            + "/scan/"
                            + type
                            + "/"
                            + URLEncoder.encode(no, StandardCharsets.UTF_8);

                    String qrPng = generateQrPngBase64(qrUrl);

                    return new OrderPlaceResponse(
                            base.getId(),
                            base.getNo(),
                            base.getType(),
                            base.getDescription(),
                            base.getStatus(),
                            base.getSeat(),
                            base.getActiveOrders(),
                            qrValue,
                            qrUrl,
                            qrPng
                    );
                })
                .toList();
    }

    public OrderPlaceResponse update(Long id, OrderPlaceRequest req) {
        OrderPlace entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + id));

        mapper.updateEntity(entity, req);
        return mapper.toResponse(entity);
    }

    public void delete(Long id) {
        OrderPlace entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + id));
        entity.setStatus(OrderPlaceStatus.DELETED);
    }
    private String generateQrPngBase64(String text) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new QRCodeWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    320,
                    320,
                    hints
            );

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);

            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());

            // return as full data URL so frontend can use directly in <img src="">
            return "data:image/png;base64," + base64;

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR PNG", e);
        }
    }
}