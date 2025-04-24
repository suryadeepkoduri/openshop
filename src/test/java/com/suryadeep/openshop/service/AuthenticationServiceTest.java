package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.entity.Cart;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
        // --- Arrange ---
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setUsername("test user");

        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName("USER");

        // No need to fully construct the 'expected' user here for the save mock
        // We only need the role for the assertion later.

        // --- Mocking ---
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // FIX: Make save return the user object it was called with
        when(userRepository.save(any(User.class))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0); // Return the first argument passed to save()
            }
        });
        // Or using a lambda (more concise):
        // when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // --- Act ---
        User registeredUser = authenticationService.registerUser(request);

        // --- Assert ---
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("test user", registeredUser.getName());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertNotNull(registeredUser.getCart()); // Good practice to check cart existence too
        assertNotNull(registeredUser.getRoles()); // Check roles is not null
        assertFalse(registeredUser.getRoles().isEmpty()); // Check roles is not empty
        // The assertEquals below should now work, comparing the Set containing the mocked 'role'
        // with the Set returned by registeredUser.getRoles()
        assertEquals(Set.of(role), registeredUser.getRoles());

        // Verify that save was indeed called
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByRoleName("USER");
        verify(passwordEncoder, times(1)).encode("password");
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
