package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.entity.Role;
import com.suryadeep.openshop.exception.EmailAlreadyExistsException;
import com.suryadeep.openshop.repository.UserRepository;
import com.suryadeep.openshop.repository.RoleRepository;
import com.suryadeep.openshop.service.implementation.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setUsername("testuser");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("testuser");

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("USER");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = authenticationService.registerUser(request);

        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("testuser", registeredUser.getName());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(Set.of(role), registeredUser.getRoles());
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setUsername("testuser");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> authenticationService.registerUser(request));
    }
}
