package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.order.OrderResponse;
import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.mapper.OrderMapper;
import com.cafeshop.demo.mapper.OrderPlaceMapper;
import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.OrderStatus;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPlaceQueryService {

    private final OrderPlaceRepository orderPlaceRepo;
    private final OrderRepository orderRepo;
    private final OrderPlaceMapper orderPlaceMapper;
    private final OrderMapper orderMapper;
    @Value("${app.cors.allowed-origins:}")
    private String frontendBaseUrl;

    @Transactional(readOnly = true)
    public List<OrderPlaceResponse> getOrderPlacesWithActiveOrders() {

        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.READY
        );

        List<OrderPlace> places = orderPlaceRepo.findAllByStatusNot(OrderPlaceStatus.DELETED);
        if (places.isEmpty()) {
            return List.of();
        }

        List<Long> placeIds = places.stream()
                .map(OrderPlace::getId)
                .toList();

        List<Order> activeOrders = orderRepo.findByOrderPlace_IdInAndStatusIn(placeIds, activeStatuses);

        Map<Long, List<OrderResponse>> activeMap = activeOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderPlace().getId(),
                        Collectors.mapping(orderMapper::toResponse, Collectors.toList())
                ));

        return places.stream()
                .map(op -> {
                    OrderPlaceResponse base = orderPlaceMapper.toResponse(op);

                    String type = op.getType();
                    String no = op.getNo();

                    String qrValue = type + ":" + no;

                    String qrUrl = frontendBaseUrl
                            + "/scan/"
                            + type
                            + "/"
                            + URLEncoder.encode(no, StandardCharsets.UTF_8);

                    String qrPng = generateQrPngBase64(qrUrl);

                    return OrderPlaceResponse.builder()
                            .id(base.getId())
                            .no(base.getNo())
                            .type(base.getType())
                            .description(base.getDescription())
                            .seat(base.getSeat())
                            .status(base.getStatus())
                            .activeOrders(activeMap.getOrDefault(op.getId(), List.of()))
                            .qrValue(qrValue)
                            .qrUrl(qrUrl)
                            .qrPng(qrPng)
                            .build();
                })
                .toList();
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