package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.CartItemRequest;
import com.suryadeep.openshop.dto.response.CartItemResponse;
import com.suryadeep.openshop.dto.response.CartResponse;
import com.suryadeep.openshop.entity.Cart;
import com.suryadeep.openshop.entity.CartItem;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.exception.ResourceNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CartItemRepository;
import com.suryadeep.openshop.repository.CartRepository;
import com.suryadeep.openshop.repository.VariantRepository;
import com.suryadeep.openshop.service.CartService;
import com.suryadeep.openshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final UserService userService;
    private final EntityMapper entityMapper;
    private final VariantRepository variantRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Override
    public CartResponse getCart() {
        User user = userService.getCurrentAuthenticatedUser();
        Cart cart = user.getCart();
        return entityMapper.toResponse(cart);
    }

    @Override
    public CartItemResponse addItemToCart(CartItemRequest cartItemRequest) {
        User user = userService.getCurrentAuthenticatedUser();
        Cart cart = user.getCart();

        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                    .filter(item -> item.getVariant().getId().equals(cartItemRequest.getVariantId()))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItemRequest.getQuantity());
                return entityMapper.toCartItemResponse(cartItem);
            }
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setQuantity(cartItemRequest.getQuantity());
        newCartItem.setVariant(variantRepository.findById(cartItemRequest.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Variant with ID %s not found", cartItemRequest.getVariantId()))));
        CartItem savedCartItem = cartItemRepository.save(newCartItem);
        cart.getCartItems().add(savedCartItem);
        cartRepository.save(cart);

        return entityMapper.toCartItemResponse(savedCartItem);
    }

    @Override
    public void removeItemFromCart(Long cartItemId) {
        User user = userService.getCurrentAuthenticatedUser();
        Cart cart = user.getCart();

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot remove item from cart.");
        }

        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
    }

    @Override
    public CartItemResponse updateItemInCart(CartItemRequest cartItemRequest, Long cartItemId) {
        User user = userService.getCurrentAuthenticatedUser();
        Cart cart = user.getCart();

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        return entityMapper.toCartItemResponse(cartItem);
    }
}
