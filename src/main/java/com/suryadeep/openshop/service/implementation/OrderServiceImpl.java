package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.OrderRequest;
import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.entity.*;
import com.suryadeep.openshop.entity.enums.OrderStatus;
import com.suryadeep.openshop.exception.ResourceNotFoundException;
import com.suryadeep.openshop.mapper.OrderMapper;
import com.suryadeep.openshop.repository.AddressRepository;
import com.suryadeep.openshop.repository.CartRepository;
import com.suryadeep.openshop.repository.OrderRepository;
import com.suryadeep.openshop.service.OrderService;
import com.suryadeep.openshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserService userService; 
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AddressRepository addressRepository;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Step 1: Get the current authenticated user.
        User user = userService.getCurrentAuthenticatedUser();

        // Step 2: Get the user's cart.
        Cart cart = user.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot create an order.");
        }

        // Step 3: Initialize the order and its details.
        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setOrderNotes(orderRequest.getOrderNotes());

        // Step 4: Calculate order totals and map cart items to order items.
        final BigDecimal[] totalItemPrice = {BigDecimal.ZERO};
        order.setOrderItems(cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());

            // Assuming price is fetched from the variant.
            BigDecimal itemPrice = cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setPrice(itemPrice);

             totalItemPrice[0] = totalItemPrice[0].add(itemPrice);
            return orderItem;
        }).collect(Collectors.toList()));

        // Step 5: Calculate tax and shipping price.
        BigDecimal taxAmount = totalItemPrice[0].multiply  (BigDecimal.valueOf(0.05)); // Assuming 5% tax
        BigDecimal shippingPrice = BigDecimal.valueOf(150); // Flat shipping price

        // Step 6: Finalize order totals.
        order.setTotalItemPrice(totalItemPrice[0]);
        order.setTaxAmount(taxAmount);
        order.setShippingPrice(shippingPrice);
        order.setTotalPrice(totalItemPrice[0].add(taxAmount).add(shippingPrice));
        Address shippingAddress = addressRepository.findById(orderRequest.getShippingAddressId()).
                orElseThrow(() -> new ResourceNotFoundException("Address Not Found"));
        order.setShippingAddress(shippingAddress);
        //TODO call payment

        // Step 7: Save the order and clear the cart.
        Order savedOrder = orderRepository.save(order);
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // Return order response.
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new IllegalStateException("Current authenticated user not found.");
        }

        List<Order> orders = orderRepository.findByUserId(user.getId());
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
                .map(order -> {
                    try {
                        return orderMapper.toResponse(order);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to map order to response", e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getUserOrdersByStatus(OrderStatus status) {
        User user = userService.getCurrentAuthenticatedUser();
        return orderRepository.findByUserAndStatus(user, status)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, int page,int size) {
        return orderRepository.findByStatus(status, Pageable.ofSize(size).withPage(page)).map(orderMapper::toResponse);
    }

    @Override
    public Page<OrderResponse> getOrders(int page, int size) {
        return orderRepository.findAll(Pageable.ofSize(size).withPage(page)).map(orderMapper::toResponse);
    }


    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if(!OrderStatus.contains(String.valueOf(newStatus))) throw new IllegalArgumentException("Invalid order status: " + newStatus);
        order.setStatus(newStatus);
        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            throw new IllegalStateException("Order is already canceled.");
        }
        if(OrderStatus.SHIPPED.equals(order.getStatus())){
            throw new IllegalStateException("Order is already shipped. Cannot cancel.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return "Order canceled successfully!";
    }

    @Override
    public boolean verifyPayment(String paymentRefNo) {
        // Placeholder implementation for payment verification.
        return paymentRefNo != null && paymentRefNo.startsWith("TXN");
    }

    
    @Override
    public byte[] downloadInvoice(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        String invoiceText = "Invoice for Order #: " + order.getOrderNumber() + "\n"
                           + "Total: " + order.getTotalPrice() + " " + order.getCurrencyCode();
        return invoiceText.getBytes();
    }
}