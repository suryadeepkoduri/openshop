package com.suryadeep.openshop.controller.authentication;

import com.suryadeep.openshop.dto.request.LoginRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.LoginResponse;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.exception.EmailAlreadyExistsException;
import com.suryadeep.openshop.security.CustomUserDetails;
import com.suryadeep.openshop.service.AuthenticationService;
import com.suryadeep.openshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

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

        when(authenticationService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        ResponseEntity<Object> response = authController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(UserResponse.fromUser(user), response.getBody());
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setUsername("testuser");

        when(authenticationService.registerUser(any(UserRegisterRequest.class))).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        ResponseEntity<Object> response = authController.registerUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    public void testAuthenticateUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(userDetails)).thenReturn("jwtToken");

        ResponseEntity<Object> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new LoginResponse("jwtToken"), response.getBody());
    }

    @Test
    public void testAuthenticateUser_BadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<Object> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
