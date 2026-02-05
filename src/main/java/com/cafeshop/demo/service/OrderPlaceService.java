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
                    // base mapping
                    OrderPlaceResponse base = mapper.toResponse(entity);

                    // computed fields
                    String type = entity.getType();
                    String no = entity.getNo();

                    String qrValue = type + ":" + no;
                    String qrUrl = frontendBaseUrl
                            + "start?type=" + type
                            + "&no=" + URLEncoder.encode(no, StandardCharsets.UTF_8);

                    // return new record with extra fields
                    return new OrderPlaceResponse(
                            base.getId(),
                            base.getNo(),
                            base.getType(),
                            base.getDescription(),
                            base.getStatus(),
                            base.getSeat(),
                            base.getActiveOrders(),
                            qrValue,
                            qrUrl
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
}