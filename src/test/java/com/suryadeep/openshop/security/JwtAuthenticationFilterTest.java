package com.suryadeep.openshop.security;

import com.suryadeep.openshop.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Captor
    private ArgumentCaptor<Exception> exceptionCaptor;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();  // Ensures clean auth state for every test

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }


    @Test
    void shouldNotAuthenticateWhenAuthHeaderIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        verifyNoInteractions(handlerExceptionResolver);
    }

    @Test
    void shouldNotAuthenticateWhenAuthHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        verifyNoInteractions(handlerExceptionResolver);
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws Exception {
        String token = "validToken";
        String userEmail = "test@example.com";
        UserDetails userDetails = new User(userEmail, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken expectedAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        expectedAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        verify(userDetailsService, times(1)).loadUserByUsername(userEmail);
        verify(jwtService, times(1)).isTokenValid(token, userDetails);
        verify(filterChain, times(1)).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() != null;
        assert SecurityContextHolder.getContext().getAuthentication().equals(expectedAuthentication);
    }

    @Test
    void shouldHandleExceptionWhenTokenIsInvalid() throws Exception {
        String token = "invalidToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid Token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver, times(1)).resolveException(eq(request), eq(response), isNull(), exceptionCaptor.capture());
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(filterChain);
        assert exceptionCaptor.getValue().getMessage().equals("Invalid Token");
    }

    @Test
    void shouldNotAuthenticateWhenUsernameIsNull() throws Exception {
        String token = "validToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void shouldSkipWhenUserAlreadyAuthenticated() throws Exception {
        String token = "validToken";
        String userEmail = "test@example.com";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(userEmail);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsExpired() throws Exception {
        String expiredToken = "expiredToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtService.extractUsername(expiredToken)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(filterChain);
    }

}