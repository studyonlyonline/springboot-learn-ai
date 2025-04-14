package com.example.springBootLearn.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Spring Boot DevTools.
 * This class helps ensure that DevTools is properly initialized and configured.
 */
@Configuration
public class DevToolsConfig {
    
    // DevTools is automatically configured by Spring Boot
    // This class serves as a marker to ensure DevTools is loaded
    
    public DevToolsConfig() {
        System.out.println("DevTools configuration initialized");
    }
}
