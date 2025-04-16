package com.example.springBootLearn.service;

import com.example.springBootLearn.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing the price list data.
 */
@Service
public class PriceListService {

    private ProductDataSource dataSource;

    @Autowired
    public PriceListService(@Qualifier("csvProductDataSource") ProductDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Set the data source to use.
     *
     * @param dataSource The data source to use
     */
    public void setDataSource(ProductDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get all products.
     *
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return dataSource.getAllProducts();
    }

    /**
     * Search products by query string.
     *
     * @param query The search query
     * @return List of products matching the query
     */
    public List<Product> searchProducts(String query) {
        List<Product> products = dataSource.getAllProducts();
        
        if (query == null || query.trim().isEmpty()) {
            return products;
        }
        
        String lowerCaseQuery = query.toLowerCase();
        
        return products.stream()
                .filter(product -> 
                        product.getName().toLowerCase().contains(lowerCaseQuery) ||
                        product.getCategory().toLowerCase().contains(lowerCaseQuery) ||
                        product.getBrand().toLowerCase().contains(lowerCaseQuery) ||
                        (product.getBarcode() != null && product.getBarcode().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    /**
     * Get unique categories.
     *
     * @return List of unique categories
     */
    public List<String> getUniqueCategories() {
        return dataSource.getAllProducts().stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Get unique brands.
     *
     * @return List of unique brands
     */
    public List<String> getUniqueBrands() {
        return dataSource.getAllProducts().stream()
                .map(Product::getBrand)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * Get product names for autocomplete.
     *
     * @return List of product names
     */
    public List<String> getProductNames() {
        return dataSource.getAllProducts().stream()
                .map(Product::getName)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * Get a product by ID.
     *
     * @param id The product ID
     * @return The product, or null if not found
     */
    public Product getProductById(String id) {
        return dataSource.getProductById(id);
    }
    
    /**
     * Save a new product.
     *
     * @param product The product to save
     * @return The saved product with ID assigned
     */
    public Product saveProduct(Product product) {
        return dataSource.saveProduct(product);
    }
    
    /**
     * Update an existing product.
     *
     * @param product The product to update
     * @return The updated product, or null if not found
     */
    public Product updateProduct(Product product) {
        return dataSource.updateProduct(product);
    }
    
    /**
     * Delete a product by ID.
     *
     * @param id The ID of the product to delete
     */
    public void deleteProduct(String id) {
        dataSource.deleteProduct(id);
    }
}
