package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration class to load common module properties.
 */
@Configuration
@PropertySource("classpath:common.properties")
public class CommonPropertiesConfig {

    /**
     * Creates a PropertySourcesPlaceholderConfigurer to resolve ${...} placeholders
     * in @Value annotations.
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
