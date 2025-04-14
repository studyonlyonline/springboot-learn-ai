package com.example.common.config;

import com.example.common.interceptor.RequestContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for the common module.
 * Registers interceptors and other web-related configurations.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestContextInterceptor requestContextInterceptor;

    @Autowired
    public WebMvcConfig(RequestContextInterceptor requestContextInterceptor) {
        this.requestContextInterceptor = requestContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the request context interceptor to be applied to all requests
        registry.addInterceptor(requestContextInterceptor);
    }
}
