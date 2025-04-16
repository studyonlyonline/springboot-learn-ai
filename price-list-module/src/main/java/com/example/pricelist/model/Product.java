package com.example.pricelist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a product in the price list.
 * Uses Lombok annotations to generate constructors, getters, setters, equals, hashCode, and toString methods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id; // Row number for CSV, document ID for Firestore
    private String name;
    private String category;
    private String brand;
    private double minimumSellingPrice;
    private double maximumSellingPrice;
    private int stockAvailability;
    private String photoUrl;
    private String barcode;
    
    /**
     * Constructor without ID (for backward compatibility)
     */
    public Product(String name, String category, String brand, 
                  double minimumSellingPrice, double maximumSellingPrice, 
                  int stockAvailability, String photoUrl, String barcode) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.minimumSellingPrice = minimumSellingPrice;
        this.maximumSellingPrice = maximumSellingPrice;
        this.stockAvailability = stockAvailability;
        this.photoUrl = photoUrl;
        this.barcode = barcode;
    }
}
