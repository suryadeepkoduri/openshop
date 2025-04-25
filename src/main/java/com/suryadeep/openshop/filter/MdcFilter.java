package com.suryadeep.openshop.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add contextual information to the MDC (Mapped Diagnostic Context)
 * This allows for enhanced logging with request-specific information
 */
@Slf4j
@Component
public class MdcFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String REQUEST_URI = "requestUri";
    private static final String REQUEST_METHOD = "requestMethod";

    private static final String UNKNOWN = "unknown";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Generate a unique request ID
            String requestId = UUID.randomUUID().toString().replace("-", "");
            MDC.put(REQUEST_ID, requestId);
            
            // Add the request URI and method
            MDC.put(REQUEST_URI, request.getRequestURI());
            MDC.put(REQUEST_METHOD, request.getMethod());
            
            // Add the client IP address
            String ipAddress = getClientIpAddress(request);
            MDC.put(IP_ADDRESS, ipAddress);
            
            // Add user information if authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                    !"anonymousUser".equals(authentication.getPrincipal().toString())) {
                MDC.put(USER_ID, authentication.getName());
            } else {
                MDC.put(USER_ID, "anonymous");
            }
            
            // Add the request ID to the response headers for correlation
            response.addHeader("X-Request-ID", requestId);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Always clear the MDC context to prevent memory leaks
            MDC.clear();
        }
    }
    
    /**
     * Extract the client IP address from the request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // If the IP address contains multiple IPs (X-Forwarded-For can have multiple IPs), take the first one
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
}