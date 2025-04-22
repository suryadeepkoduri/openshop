package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.entity.Cart;
import com.suryadeep.openshop.entity.Role;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.exception.EmailAlreadyExistsException;
import com.suryadeep.openshop.repository.CartRepository;
import com.suryadeep.openshop.repository.RoleRepository;
import com.suryadeep.openshop.repository.UserRepository;
import com.suryadeep.openshop.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;

    @Transactional
    public User registerUser(UserRegisterRequest registerRequest) throws EmailAlreadyExistsException {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Set default role for new user
        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
        user.setRoles(Set.of(defaultRole));

        Cart cart = new Cart();

        user.setCart(cart);

        User createdUser = userRepository.save(user);
        cart.setUser(createdUser);
        cartRepository.save(cart);
        return createdUser;
    }
}