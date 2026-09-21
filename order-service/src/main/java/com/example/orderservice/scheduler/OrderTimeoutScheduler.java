package com.example.orderservice.scheduler;

import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderTimeoutScheduler {

    private final OrderRepository orderRepository;

    public OrderTimeoutScheduler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Chạy mỗi phút để quét các đơn hàng PENDING quá 5 phút
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkPendingOrdersTimeout() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(5);
        List<Order> pendingOrders = orderRepository.findByStatusAndCreatedAtBefore("PENDING", timeoutThreshold);

        for (Order order : pendingOrders) {
            order.setStatus("FAILED");
            orderRepository.save(order);
            // Gửi thông báo hoặc thực hiện hoàn kho nếu cần
        }
    }
}
