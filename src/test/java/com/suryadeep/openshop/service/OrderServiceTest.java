package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.OrderRequest;
import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.entity.*;
import com.suryadeep.openshop.entity.enums.OrderStatus;
import com.suryadeep.openshop.exception.ResourceNotFoundException;
import com.suryadeep.openshop.mapper.OrderMapper;
import com.suryadeep.openshop.repository.AddressRepository;
import com.suryadeep.openshop.repository.CartRepository;
import com.suryadeep.openshop.repository.OrderRepository;
import com.suryadeep.openshop.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        User user = new User();
        Cart cart = new Cart();
        cart.setCartItems(Collections.singletonList(new CartItem()));
        user.setCart(cart);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setShippingAddressId(1L);
        orderRequest.setPaymentMethod("Credit Card");
        orderRequest.setOrderNotes("Please deliver between 5-6 PM");

        Address address = new Address();
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testGetOrder() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(new OrderResponse());

        OrderResponse response = orderService.getOrder(1L);

        assertNotNull(response);
    }

    @Test
    void testCancelOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        String result = orderService.cancelOrder(1L);

        assertEquals("Order canceled successfully!", result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void testGetUserOrders() {
        User user = new User();
        user.setId(1L);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(orderRepository.findByUserId(1L)).thenReturn(Collections.singletonList(new Order()));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        List<OrderResponse> responses = orderService.getUserOrders();

        assertFalse(responses.isEmpty());
    }

    @Test
    void testDownloadInvoice() {
        Order order = new Order();
        order.setOrderNumber("ORD-12345");
        order.setTotalPrice(BigDecimal.valueOf(1000));
        order.setCurrencyCode("INR");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        byte[] invoice = orderService.downloadInvoice(1L);

        assertNotNull(invoice);
        assertTrue(new String(invoice).contains("ORD-12345"));
    }
}
