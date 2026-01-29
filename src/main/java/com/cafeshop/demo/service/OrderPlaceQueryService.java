package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.OrderStatus;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPlaceQueryService {

    private final OrderPlaceRepository repo;

    public List<OrderPlaceResponse> getAllWithCurrentOrder() {

        List<OrderStatus> active = List.of(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.READY
        );

        return repo.findOrderPlacesWithCurrentOrder(active).stream().map(r -> {

            Long id = (Long) r[0];
            String no = (String) r[1];
            String type = (String) r[2];
            String description = (String) r[3];
            Integer seat = (Integer) r[4];
            OrderPlaceStatus status = (OrderPlaceStatus) r[5];

            Long currentOrderId = (Long) r[6];
            OrderStatus currentOrderStatus = (OrderStatus) r[7];

            return OrderPlaceResponse.builder()
                    .id(id)
                    .no(no)
                    .type(type)
                    .description(description)
                    .seat(seat)
                    .status(status)
                    .currentOrderId(currentOrderId)
                    .currentOrderStatus(currentOrderStatus)
                    .build();
        }).toList();
    }
}