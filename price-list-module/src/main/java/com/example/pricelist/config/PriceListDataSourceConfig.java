package com.example.pricelist.config;

import com.example.pricelist.service.ProductDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for data source selection.
 */
@Configuration
public class PriceListDataSourceConfig {
    
    /**
     * Configure the default product data source.
     * 
     * @param csvDataSource The CSV product data source
     * @return The default product data source
     */
    @Bean
    @Primary
    public ProductDataSource defaultProductDataSource(
            @Qualifier("csvProductDataSource") ProductDataSource csvDataSource) {
        return csvDataSource;
    }
}
