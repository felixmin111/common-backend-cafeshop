package com.cafeshop.demo.service.webSocket;

import com.cafeshop.demo.dto.payment.PaymentUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void paymentUpdated(PaymentUpdateEvent evt) {

        // Notify each order details page
        for (Long orderId : evt.orderIds()) {
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + orderId + "/payment",
                    evt
            );
        }

        // Optional: notify orders list page to refresh
        messagingTemplate.convertAndSend("/topic/orders/payment", evt);
    }
}
