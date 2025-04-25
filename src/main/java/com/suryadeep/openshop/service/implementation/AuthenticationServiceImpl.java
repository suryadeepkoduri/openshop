package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.entity.Cart;
import com.suryadeep.openshop.entity.Role;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.exception.EmailAlreadyExistsException;
import com.suryadeep.openshop.exception.ResourceNotFoundException;
import com.suryadeep.openshop.repository.CartRepository;
import com.suryadeep.openshop.repository.RoleRepository;
import com.suryadeep.openshop.repository.UserRepository;
import com.suryadeep.openshop.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;

    @Transactional
    public User registerUser(UserRegisterRequest registerRequest) throws EmailAlreadyExistsException {
        logger.info("Registering new user with email: {}", registerRequest.getEmail());

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            logger.error("Email already exists: {}", registerRequest.getEmail());
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Set default role for new user
        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        user.setRoles(Set.of(defaultRole));

        Cart cart = new Cart();
        user.setCart(cart);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with email: {}", registerRequest.getEmail());
        return savedUser;
    }
}
