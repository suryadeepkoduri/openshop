package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.OrderRequest;
import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        OrderResponse orderResponse = new OrderResponse();

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        ResponseEntity<Object> responseEntity = orderController.createOrder(orderRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderResponse, responseEntity.getBody());
    }

    @Test
    public void testGetOrder() {
        Long orderId = 1L;
        OrderResponse orderResponse = new OrderResponse();

        when(orderService.getOrder(orderId)).thenReturn(orderResponse);

        ResponseEntity<Object> responseEntity = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(orderResponse, responseEntity.getBody());
    }

    @Test
    public void testCancelOrder() {
        Long orderId = 1L;
        String cancelMessage = "Order cancelled successfully";

        when(orderService.cancelOrder(orderId)).thenReturn(cancelMessage);

        ResponseEntity<Object> responseEntity = orderController.cancelOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(cancelMessage, responseEntity.getBody());
    }

    @Test
    public void testGetUserOrders() {
        OrderResponse order1 = new OrderResponse(); // you can set fields if needed
        OrderResponse order2 = new OrderResponse();
        List<OrderResponse> mockOrders = List.of(order1, order2);

        when(orderService.getUserOrders()).thenReturn(mockOrders);

        ResponseEntity<Object> responseEntity = orderController.getUserOrders();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockOrders, responseEntity.getBody());
    }

    @Test
    public void testDownloadInvoice() {
        Long orderId = 1L;
        byte[] invoice = new byte[0];

        when(orderService.downloadInvoice(orderId)).thenReturn(invoice);

        ResponseEntity<Object> responseEntity = orderController.downloadInvoice(orderId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(invoice, responseEntity.getBody());
    }
}
