package com.example.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interceptor to add basic context information to each request.
 * This can be used to track request information, add common attributes, etc.
 */
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String REQUEST_TIMESTAMP_ATTRIBUTE = "requestTimestamp";
    private static final String USER_AGENT_ATTRIBUTE = "userAgent";
    private static final String REQUEST_PATH_ATTRIBUTE = "requestPath";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        
        // Add timestamp
        LocalDateTime timestamp = LocalDateTime.now();
        request.setAttribute(REQUEST_TIMESTAMP_ATTRIBUTE, timestamp);
        
        // Add user agent
        String userAgent = request.getHeader("User-Agent");
        request.setAttribute(USER_AGENT_ATTRIBUTE, userAgent);
        
        // Add request path
        String requestPath = request.getRequestURI();
        request.setAttribute(REQUEST_PATH_ATTRIBUTE, requestPath);
        
        // Add request ID to response headers for tracking
        response.setHeader("X-Request-ID", requestId);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Add common attributes to ModelAndView if it's not null
        if (modelAndView != null) {
            modelAndView.addObject(REQUEST_ID_ATTRIBUTE, request.getAttribute(REQUEST_ID_ATTRIBUTE));
            modelAndView.addObject(REQUEST_TIMESTAMP_ATTRIBUTE, request.getAttribute(REQUEST_TIMESTAMP_ATTRIBUTE));
        }
    }
}
