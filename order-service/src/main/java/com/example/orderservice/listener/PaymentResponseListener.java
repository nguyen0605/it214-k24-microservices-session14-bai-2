package com.example.orderservice.listener;

import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentResponseListener {

    private final OrderRepository orderRepository;

    public PaymentResponseListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventListener
    @Transactional
    public void handlePaymentResponse(PaymentResponseEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        // Bỏ qua nếu đơn hàng đã được xử lý trước đó để tránh ghi đè trạng thái
        if (!"PENDING".equals(order.getStatus())) {
            return;
        }

        switch (event.getStatus()) {
            case "SUCCESS":
                order.setStatus("PAID");
                break;
            case "REJECTED":
                order.setStatus("CANCELED");
                // Thực hiện hoàn kho (nếu cần)
                break;
            case "FAILED":
                order.setStatus("FAILED");
                break;
            default:
                order.setStatus("UNKNOWN");
                break;
        }
        orderRepository.save(order);
    }
}
