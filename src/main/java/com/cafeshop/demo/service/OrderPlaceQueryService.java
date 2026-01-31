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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<OrderPlaceResponse> getOrderPlacesWithActiveOrders() {

        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.READY
        );

        List<OrderPlace> places = orderPlaceRepo.findAllByStatusNot(OrderPlaceStatus.DELETED);
        if (places.isEmpty()) return List.of();

        List<Long> placeIds = places.stream().map(OrderPlace::getId).toList();

        List<Order> activeOrders =
                orderRepo.findByOrderPlace_IdInAndStatusIn(placeIds, activeStatuses);

        // group: placeId -> List<OrderResponse>
        Map<Long, List<OrderResponse>> activeMap =
                activeOrders.stream()
                        .collect(Collectors.groupingBy(
                                o -> o.getOrderPlace().getId(),
                                Collectors.mapping(orderMapper::toResponse, Collectors.toList())
                        ));

        return places.stream().map(op -> {
            OrderPlaceResponse base = orderPlaceMapper.toResponse(op);

            return OrderPlaceResponse.builder()
                    .id(base.getId())
                    .no(base.getNo())
                    .type(base.getType())
                    .description(base.getDescription())
                    .seat(base.getSeat())
                    .status(base.getStatus())
                    .activeOrders(activeMap.getOrDefault(op.getId(), List.of()))
                    .build();
        }).toList();
    }
}