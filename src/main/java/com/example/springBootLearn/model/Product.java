package com.example.springBootLearn.model;

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
    private String name;
    private String category;
    private String brand;
    private double minimumSellingPrice;
    private double maximumSellingPrice;
    private int stockAvailability;
    private String photoUrl;
    private String barcode;
}
