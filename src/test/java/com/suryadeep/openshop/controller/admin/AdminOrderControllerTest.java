package com.suryadeep.openshop.controller.admin;

import com.suryadeep.openshop.entity.enums.OrderStatus;
import com.suryadeep.openshop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.suryadeep.openshop.dto.response.OrderResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminOrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private AdminOrderController adminOrderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateOrderStatus() {
        Long orderId = 1L;
        OrderStatus status = OrderStatus.SHIPPED;

        OrderResponse mockResponse = new OrderResponse();
        when(orderService.updateOrderStatus(orderId, status)).thenReturn(mockResponse);

        ResponseEntity<Object> response = adminOrderController.updateOrderStatus(orderId, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(orderService, times(1)).updateOrderStatus(orderId, status);
    }

//    @Test
//    public void testVerifyPayment() {
//        Long orderId = 1L;
//        String paymentId = "TXN123";
//
//        when(orderService.verifyPayment(paymentId)).thenReturn(true);
//
//        ResponseEntity<Object> response = adminOrderController.verifyPayment(orderId, paymentId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Payment verified", response.getBody());
//        verify(orderService, times(1)).verifyPayment(paymentId);
//    }

    @Test
    public void testGetOrders() {
        int page = 0;
        int size = 10;

        Page<OrderResponse> mockPage = mock(Page.class);
        when(orderService.getOrders(page, size)).thenReturn(mockPage);

        ResponseEntity<Object> response = adminOrderController.getOrders(page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
        verify(orderService, times(1)).getOrders(page, size);
    }

    @Test
    public void testGetOrderByStatus() {
        OrderStatus status = OrderStatus.PENDING;
        int page = 0;
        int size = 10;

        Page<OrderResponse> mockPage = mock(Page.class);
        when(orderService.getOrdersByStatus(status, page, size)).thenReturn(mockPage);

        ResponseEntity<Object> response = adminOrderController.getOrderByStatus(status, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
        verify(orderService, times(1)).getOrdersByStatus(status, page, size);
    }
}
