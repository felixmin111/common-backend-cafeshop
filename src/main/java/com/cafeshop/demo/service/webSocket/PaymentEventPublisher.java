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

        System.out.println("========== PAYMENT EVENT ==========");
        System.out.println("Payment ID: " + evt.paymentId());
        System.out.println("Order IDs: " + evt.orderIds());
        System.out.println("===================================");

        for (Long orderId : evt.orderIds()) {

            String destination = "/topic/orders/" + orderId + "/payment";

            System.out.println("Sending to: " + destination);

            messagingTemplate.convertAndSend(destination, evt);
        }

        String paymentTopic = "/topic/payments/" + evt.paymentId();
        System.out.println("Sending to: " + paymentTopic);

        messagingTemplate.convertAndSend(paymentTopic, evt);

        System.out.println("Sending to: /topic/orders/payment");

        messagingTemplate.convertAndSend("/topic/orders/payment", evt);

        System.out.println("===== MESSAGE SENT SUCCESSFULLY =====");
    }
}
