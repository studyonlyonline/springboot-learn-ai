package com.example.springBootLearn.service;

import com.example.springBootLearn.model.Product;
import java.util.List;

/**
 * Interface for product data sources.
 * Implementations can retrieve data from different sources (CSV, Firestore, etc.)
 */
public interface ProductDataSource {
    /**
     * Get all products.
     *
     * @return List of all products
     */
    List<Product> getAllProducts();
    
    /**
     * Get a product by ID.
     *
     * @param id The product ID
     * @return The product, or null if not found
     */
    Product getProductById(String id);
    
    /**
     * Save a new product.
     *
     * @param product The product to save
     * @return The saved product with ID assigned
     */
    Product saveProduct(Product product);
    
    /**
     * Update an existing product.
     *
     * @param product The product to update
     * @return The updated product, or null if not found
     */
    Product updateProduct(Product product);
    
    /**
     * Delete a product by ID.
     *
     * @param id The ID of the product to delete
     */
    void deleteProduct(String id);
}
