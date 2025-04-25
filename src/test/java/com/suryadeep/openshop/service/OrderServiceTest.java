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
            // Set up user and cart
            User user = new User();
            Cart cart = new Cart();

            // Create and set a valid variant for the cart item
            Variant variant = new Variant();
            variant.setPrice(BigDecimal.valueOf(500)); // Set price for the variant

            CartItem cartItem = new CartItem();
            cartItem.setVariant(variant); // Ensure the variant is not null
            cartItem.setQuantity(1); // Set quantity for the cart item

            cart.setCartItems(Collections.singletonList(cartItem)); // Add cart item to cart
            user.setCart(cart); // Assign the cart to the user

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
        void testCreateOrderWithInvalidShippingAddress() {
            User user = new User();
            Cart cart = new Cart();

            CartItem cartItem = new CartItem();
            Variant variant = new Variant();
            variant.setPrice(BigDecimal.valueOf(400));
            cartItem.setVariant(variant);
            cartItem.setQuantity(1);

            cart.setCartItems(Collections.singletonList(cartItem));
            user.setCart(cart);

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setShippingAddressId(999L); // Invalid ID

            when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
            when(addressRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequest));
            verify(addressRepository, times(1)).findById(anyLong());
        }

        @Test
        void testCreateOrderWithEmptyCart() {
            User user = new User();
            Cart cart = new Cart();

            cart.setCartItems(Collections.emptyList());
            user.setCart(cart);

            OrderRequest orderRequest = new OrderRequest();

            when(userService.getCurrentAuthenticatedUser()).thenReturn(user);

            assertThrows(IllegalStateException.class, () -> orderService.createOrder(orderRequest));
            verify(userService, times(1)).getCurrentAuthenticatedUser();
        }

        @Test
        void testCreateOrderWithNullCartVariant() {
            User user = new User();
            Cart cart = new Cart();

            CartItem cartItem = new CartItem();
            cartItem.setVariant(null); // Null variant
            cartItem.setQuantity(1);

            cart.setCartItems(Collections.singletonList(cartItem));
            user.setCart(cart);

            OrderRequest orderRequest = new OrderRequest();

            when(userService.getCurrentAuthenticatedUser()).thenReturn(user);

            assertThrows(IllegalStateException.class, () -> orderService.createOrder(orderRequest));
            verify(userService, times(1)).getCurrentAuthenticatedUser();
        }

        @Test
        void testCreateOrderWithNoCartAssignedToUser() {
            User user = new User();
            user.setCart(null); // No cart assigned

            OrderRequest orderRequest = new OrderRequest();

            when(userService.getCurrentAuthenticatedUser()).thenReturn(user);

            assertThrows(IllegalStateException.class, () -> orderService.createOrder(orderRequest));
            verify(userService, times(1)).getCurrentAuthenticatedUser();
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
