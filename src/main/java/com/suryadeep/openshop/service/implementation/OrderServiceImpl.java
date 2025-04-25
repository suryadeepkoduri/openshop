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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final UserService userService; 
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AddressRepository addressRepository;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        logger.info("Creating order for user with shipping address ID: {}", orderRequest.getShippingAddressId());

        User user = userService.getCurrentAuthenticatedUser();
        Cart cart = user.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            logger.error("Cart is empty. Cannot create an order.");
            throw new IllegalStateException("Cart is empty. Cannot create an order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setOrderNotes(orderRequest.getOrderNotes());

        final BigDecimal[] totalItemPrice = {BigDecimal.ZERO};
        order.setOrderItems(cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());

            if (cartItem.getVariant() == null) {
                logger.error("Cart item variant is missing. Cannot proceed with order creation.");
                throw new IllegalStateException("Cart item variant is missing. Cannot proceed with order creation.");
            }

            BigDecimal itemPrice = cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setPrice(itemPrice);

            totalItemPrice[0] = totalItemPrice[0].add(itemPrice);
            return orderItem;
        }).collect(Collectors.toList()));

        BigDecimal taxAmount = totalItemPrice[0].multiply(BigDecimal.valueOf(0.05));
        BigDecimal shippingPrice = BigDecimal.valueOf(150);

        order.setTotalItemPrice(totalItemPrice[0]);
        order.setTaxAmount(taxAmount);
        order.setShippingPrice(shippingPrice);
        order.setTotalPrice(totalItemPrice[0].add(taxAmount).add(shippingPrice));
        Address shippingAddress = addressRepository.findById(orderRequest.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Address with ID %s not found", orderRequest.getShippingAddressId())));
        order.setShippingAddress(shippingAddress);

        Order savedOrder = orderRepository.save(order);
        cart.setCartItems(new ArrayList<>(cart.getCartItems()));
        cart.getCartItems().clear();
        cartRepository.save(cart);

        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        logger.info("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            logger.error("Current authenticated user not found.");
            throw new IllegalStateException("Current authenticated user not found.");
        }

        List<Order> orders = orderRepository.findByUserId(user.getId());
        if (orders == null || orders.isEmpty()) {
            logger.info("No orders found for user with ID: {}", user.getId());
            return Collections.emptyList();
        }

        return orders.stream()
                .map(order -> {
                    try {
                        return orderMapper.toResponse(order);
                    } catch (Exception e) {
                        logger.error("Failed to map order to response", e);
                        throw new IllegalStateException("Failed to map order to response", e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getUserOrdersByStatus(OrderStatus status) {
        User user = userService.getCurrentAuthenticatedUser();
        logger.info("Fetching orders for user with ID: {} and status: {}", user.getId(), status);
        return orderRepository.findByUserAndStatus(user, status)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        logger.info("Fetching orders with status: {}, page: {}, and size: {}", status, page, size);
        return orderRepository.findByStatus(status, Pageable.ofSize(size).withPage(page)).map(orderMapper::toResponse);
    }

    @Override
    public Page<OrderResponse> getOrders(int page, int size) {
        logger.info("Fetching orders with page: {} and size: {}", page, size);
        return orderRepository.findAll(Pageable.ofSize(size).withPage(page)).map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("Updating order status for order ID: {} to {}", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (!OrderStatus.contains(String.valueOf(newStatus))) {
            logger.error("Invalid order status: {}", newStatus);
            throw new IllegalArgumentException("Invalid order status: " + newStatus);
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
        logger.info("Order status updated successfully for order ID: {}", orderId);
        return orderMapper.toResponse(order);
    }

    @Override
    public String cancelOrder(Long orderId) {
        logger.info("Cancelling order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            logger.warn("Order is already canceled.");
            throw new IllegalStateException("Order is already canceled.");
        }
        if (OrderStatus.SHIPPED.equals(order.getStatus())) {
            logger.warn("Order is already shipped. Cannot cancel.");
            throw new IllegalStateException("Order is already shipped. Cannot cancel.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        logger.info("Order canceled successfully with ID: {}", orderId);
        return "Order canceled successfully!";
    }

    @Override
    public boolean verifyPayment(String paymentRefNo) {
        logger.info("Verifying payment with reference number: {}", paymentRefNo);
        return paymentRefNo != null && paymentRefNo.startsWith("TXN");
    }

    @Override
    public byte[] downloadInvoice(Long orderId) {
        logger.info("Downloading invoice for order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        String invoiceText = "Invoice for Order #: " + order.getOrderNumber() + "\n"
                           + "Total: " + order.getTotalPrice() + " " + order.getCurrencyCode();
        return invoiceText.getBytes();
    }
}
