package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.CartItemRequest;
import com.suryadeep.openshop.dto.response.CartItemResponse;
import com.suryadeep.openshop.dto.response.CartResponse;
import com.suryadeep.openshop.entity.Cart;
import com.suryadeep.openshop.entity.CartItem;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.entity.Variant;
import com.suryadeep.openshop.exception.ResourceNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CartItemRepository;
import com.suryadeep.openshop.repository.CartRepository;
import com.suryadeep.openshop.repository.ProductRepository;
import com.suryadeep.openshop.repository.VariantRepository;
import com.suryadeep.openshop.service.implementation.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private UserService userService;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCart() {
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(entityMapper.toResponse(cart)).thenReturn(new CartResponse());

        CartResponse cartResponse = cartService.getCart();

        assertNotNull(cartResponse);
        verify(userService, times(1)).getCurrentAuthenticatedUser();
        verify(entityMapper, times(1)).toResponse(cart);
    }

    @Test
    public void testAddItemToCart() {
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setVariantId(1L);
        cartItemRequest.setQuantity(2);

        Variant variant = new Variant();
        variant.setId(1L);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(entityMapper.toCartItemResponse(any(CartItem.class))).thenReturn(new CartItemResponse());

        CartItemResponse cartItemResponse = cartService.addItemToCart(cartItemRequest);

        assertNotNull(cartItemResponse);
        verify(userService, times(1)).getCurrentAuthenticatedUser();
        verify(variantRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(entityMapper, times(1)).toCartItemResponse(any(CartItem.class));
    }

    @Test
    public void testAddItemToCartWithNonExistentVariant() {
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setVariantId(2L);
        cartItemRequest.setQuantity(3);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(variantRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                cartService.addItemToCart(cartItemRequest)
        );

        assertEquals("Variant with ID 2 not found", exception.getMessage());
        verify(userService, times(1)).getCurrentAuthenticatedUser();
        verify(variantRepository, times(1)).findById(2L);
        verifyNoInteractions(cartRepository, entityMapper);
    }

    @Test
    public void testAddItemToCartWithCorrectQuantity() {
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setVariantId(3L);
        cartItemRequest.setQuantity(5);

        Variant variant = new Variant();
        variant.setId(3L);
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(5);
        cartItem.setVariant(variant);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(variantRepository.findById(3L)).thenReturn(Optional.of(variant));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(entityMapper.toCartItemResponse(cartItem)).thenReturn(new CartItemResponse());

        CartItemResponse cartItemResponse = cartService.addItemToCart(cartItemRequest);

        assertNotNull(cartItemResponse);
        assertEquals(5, cartItem.getQuantity());
        verify(userService, times(1)).getCurrentAuthenticatedUser();
        verify(variantRepository, times(1)).findById(3L);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(entityMapper, times(1)).toCartItemResponse(cartItem);
    }

    @Test
    public void testUpdateItemInCart() {
        User user = new User();
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(1);
        cart.setCartItems(Collections.singletonList(cartItem));
        user.setCart(cart);

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setQuantity(2);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(entityMapper.toCartItemResponse(any(CartItem.class))).thenReturn(new CartItemResponse());

        CartItemResponse cartItemResponse = cartService.updateItemInCart(cartItemRequest, 1L);

        assertNotNull(cartItemResponse);
        assertEquals(2, cartItem.getQuantity());
        verify(userService, times(1)).getCurrentAuthenticatedUser();
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(entityMapper, times(1)).toCartItemResponse(any(CartItem.class));
    }

    @Test
    public void testRemoveItemFromCart() {
        User user = new User();
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);

        // Use a mutable ArrayList instead of singletonList
        cart.setCartItems(new ArrayList<>(Collections.singletonList(cartItem)));
        user.setCart(cart);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Call the method to remove the item
        cartService.removeItemFromCart(1L);

        // Verify that the cart items list is empty after removal
        assertTrue(cart.getCartItems().isEmpty());
        verify(userService, times(1)).getCurrentAuthenticatedUser();
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}
