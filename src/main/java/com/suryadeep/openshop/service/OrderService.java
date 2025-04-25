package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.OrderRequest;
import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse getOrder(Long orderId);
    List<OrderResponse> getUserOrders();
    List<OrderResponse> getUserOrdersByStatus(OrderStatus status);
    Page<OrderResponse> getOrdersByStatus(OrderStatus status, int page,int size); // Admin
    Page<OrderResponse> getOrders(int page,int size);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus); // Admin
    String cancelOrder(Long orderId);

    boolean verifyPayment(String paymentRefNo);
    byte[] downloadInvoice(Long orderId);
}
