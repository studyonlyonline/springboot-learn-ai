package com.example.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the common module.
 * This enables component scanning for all classes in the common module.
 */
@Configuration
@ComponentScan(basePackages = "com.example.common")
public class CommonModuleConfiguration {
    // Configuration is handled by annotations
}
