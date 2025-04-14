package com.example.springBootLearn.service;

import com.example.springBootLearn.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing the price list data.
 */
@Service
public class PriceListService {

    private List<Product> products;

    public PriceListService() {
        this.products = loadProductsFromCsv();
    }

    /**
     * Load products from the CSV file.
     *
     * @return List of products
     */
    private List<Product> loadProductsFromCsv() {
        List<Product> productList = new ArrayList<>();
        
        try {
            ClassPathResource resource = new ClassPathResource("data/products.csv");
            Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
            
            for (CSVRecord csvRecord : csvParser) {
                Product product = new Product(
                        csvRecord.get("name"),
                        csvRecord.get("category"),
                        csvRecord.get("brand"),
                        Double.parseDouble(csvRecord.get("minimumSellingPrice")),
                        Double.parseDouble(csvRecord.get("maximumSellingPrice")),
                        Integer.parseInt(csvRecord.get("stockAvailability")),
                        csvRecord.get("photoUrl"),
                        csvRecord.get("barcode")
                );
                productList.add(product);
            }
            
            csvParser.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return productList;
    }

    /**
     * Get all products.
     *
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * Search products by query string.
     *
     * @param query The search query
     * @return List of products matching the query
     */
    public List<Product> searchProducts(String query) {
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
        return products.stream()
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
        return products.stream()
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
        return products.stream()
                .map(Product::getName)
                .distinct()
                .collect(Collectors.toList());
    }
}
