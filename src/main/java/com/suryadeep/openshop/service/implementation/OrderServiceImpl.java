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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserService userService; 
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AddressRepository addressRepository;

    private static final String ORDER_NOT_FOUND_MSG = "Order with ID %s not found";

    @Override
    @CacheEvict(value = "orders", key = "'user_' + #root.target.getCurrentUserId()")
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating order for user with shipping address ID: {}", orderRequest.getShippingAddressId());

        User user = userService.getCurrentAuthenticatedUser();
        Cart cart = user.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            log.error("Cart is empty. Cannot create an order.");
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
                log.error("Cart item variant is missing. Cannot proceed with order creation.");
                throw new IllegalStateException("Cart item variant is missing. Cannot proceed with order creation.");
            }

            BigDecimal itemPrice = cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setPrice(itemPrice);

            totalItemPrice[0] = totalItemPrice[0].add(itemPrice);
            return orderItem;
        }).toList());

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

        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponse getOrder(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(String.format(ORDER_NOT_FOUND_MSG, orderId))));
        return orderMapper.toResponse(order);
    }

    @Override
    @Cacheable(value = "orders", key = "'user_' + #root.target.getCurrentUserId()")
    public List<OrderResponse> getUserOrders() {
        User user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            log.error("Current authenticated user not found.");
            throw new IllegalStateException("Current authenticated user not found.");
        }

        List<Order> orders = orderRepository.findByUserId(user.getId());
        if (orders == null || orders.isEmpty()) {
            log.info("No orders found for user with ID: {}", user.getId());
            return Collections.emptyList();
        }

        return orders.stream()
                .map(order -> {
                    try {
                        return orderMapper.toResponse(order);
                    } catch (Exception e) {
                        log.error("Failed to map order to response", e);
                        throw new IllegalStateException("Failed to map order to response", e);
                    }
                }).toList();
    }

    @Override
    @Cacheable(value = "orders", key = "'user_' + #root.target.getCurrentUserId() + '_status_' + #status")
    public List<OrderResponse> getUserOrdersByStatus(OrderStatus status) {
        User user = userService.getCurrentAuthenticatedUser();
        log.info("Fetching orders for user with ID: {} and status: {}", user.getId(), status);
        return orderRepository.findByUserAndStatus(user, status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "orders", key = "'status_' + #status + '_page_' + #page + '_size_' + #size")
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        log.info("Fetching orders with status: {}, page: {}, and size: {}", status, page, size);
        return orderRepository.findByStatus(status, Pageable.ofSize(size).withPage(page)).map(orderMapper::toResponse);
    }

    @Override
    @Cacheable(value = "orders", key = "'all_page_' + #page + '_size_' + #size")
    public Page<OrderResponse> getOrders(int page, int size) {
        log.info("Fetching orders with page: {} and size: {}", page, size);
        return orderRepository.findAll(Pageable.ofSize(size).withPage(page)).map(orderMapper::toResponse);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "orders", key = "#orderId"),
        @CacheEvict(value = "orders", key = "'user_' + #root.target.getUserIdFromOrder(#orderId)"),
        @CacheEvict(value = "orders", key = "'all_page_0_size_10'")
        // We evict the status-specific caches in the method body
    })
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order status for order ID: {} to {}", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ORDER_NOT_FOUND_MSG, orderId)));
        if (!OrderStatus.contains(String.valueOf(newStatus))) {
            log.error("Invalid order status: {}", newStatus);
            throw new IllegalArgumentException("Invalid order status: " + newStatus);
        }

        // Store old status for cache eviction
        OrderStatus oldStatus = order.getStatus();

        order.setStatus(newStatus);
        orderRepository.save(order);

        // Evict cache for old status if different from new status
        if (oldStatus != null && !oldStatus.equals(newStatus)) {
            log.debug("Order status changed from {} to {}. Evicting old status cache.", oldStatus, newStatus);
            evictStatusCaches(oldStatus);

            // Also evict user's status-specific order cache
            Long userId = order.getUser().getId();
            evictUserStatusCaches(userId, oldStatus);

            // Evict cache for new status
            evictStatusCaches(newStatus);
            evictUserStatusCaches(userId, newStatus);
        }

        log.info("Order status updated successfully for order ID: {}", orderId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "orders", key = "#orderId"),
        @CacheEvict(value = "orders", key = "'user_' + #root.target.getUserIdFromOrder(#orderId)"),
        @CacheEvict(value = "orders", key = "'all_page_0_size_10'")
        // We evict the status-specific caches in the method body
    })
    public String cancelOrder(Long orderId) {
        log.info("Cancelling order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            log.warn("Order is already canceled.");
            throw new IllegalStateException("Order is already canceled.");
        }
        if (OrderStatus.SHIPPED.equals(order.getStatus())) {
            log.warn("Order is already shipped. Cannot cancel.");
            throw new IllegalStateException("Order is already shipped. Cannot cancel.");
        }

        // Store old status for cache eviction
        OrderStatus oldStatus = order.getStatus();

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Evict cache for old status
        if (oldStatus != null && !oldStatus.equals(OrderStatus.CANCELLED)) {
            log.debug("Order status changed from {} to CANCELLED. Evicting old status cache.", oldStatus);
            evictStatusCaches(oldStatus);

            // Also evict user's status-specific order cache
            Long userId = order.getUser().getId();
            evictUserStatusCaches(userId, oldStatus);

            // Evict cache for CANCELLED status
            evictStatusCaches(OrderStatus.CANCELLED);
            evictUserStatusCaches(userId, OrderStatus.CANCELLED);
        }

        log.info("Order canceled successfully with ID: {}", orderId);
        return "Order canceled successfully!";
    }

    @Override
    public boolean verifyPayment(String paymentRefNo) {
        log.info("Verifying payment with reference number: {}", paymentRefNo);
        return paymentRefNo != null && paymentRefNo.startsWith("TXN");
    }

    @Override
    @Cacheable(value = "orders", key = "'invoice_' + #orderId")
    public byte[] downloadInvoice(Long orderId) {
        log.info("Downloading invoice for order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ORDER_NOT_FOUND_MSG, orderId)));
        String invoiceText = "Invoice for Order #: " + order.getOrderNumber() + "\n"
                           + "Total: " + order.getTotalPrice() + " " + order.getCurrencyCode();
        return invoiceText.getBytes();
    }

    /**
     * Helper method to get the ID of the currently authenticated user.
     * Used for cache key generation.
     * 
     * @return the ID of the currently authenticated user
     */
    public Long getCurrentUserId() {
        User user = userService.getCurrentAuthenticatedUser();
        return user.getId();
    }

    /**
     * Helper method to get the user ID from an order.
     * Used for cache key generation.
     * 
     * @param orderId the ID of the order
     * @return the ID of the user who placed the order
     */
    public Long getUserIdFromOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ORDER_NOT_FOUND_MSG, orderId)));
        return order.getUser().getId();
    }

    /**
     * Helper method to evict caches related to a specific order status.
     * This is used when an order's status changes.
     * 
     * @param status the order status whose caches should be evicted
     */
    @CacheEvict(value = "orders", key = "'status_' + #status + '_page_0_size_10'")
    public void evictStatusCaches(OrderStatus status) {
        log.debug("Evicting caches for order status: {}", status);
        // This method is annotated with @CacheEvict to evict the basic status cache for the first page

        // In a real-world scenario, you might want to be more sophisticated about which pages to evict
        // For example, you could evict caches for multiple page sizes and page numbers
    }

    /**
     * Helper method to evict user-specific caches related to a specific order status.
     * This is used when an order's status changes.
     * 
     * @param userId the ID of the user whose status-specific caches should be evicted
     * @param status the order status whose caches should be evicted
     */
    @CacheEvict(value = "orders", key = "'user_' + #userId + '_status_' + #status")
    public void evictUserStatusCaches(Long userId, OrderStatus status) {
        log.debug("Evicting user-specific caches for user ID: {} and order status: {}", userId, status);
        // This method is annotated with @CacheEvict to evict the user's status-specific order cache
    }
}
