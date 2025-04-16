package com.example.springBootLearn.service;

import com.example.springBootLearn.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ProductDataSource that reads/writes from/to a CSV file.
 */
@Component("csvProductDataSource")
public class CsvProductDataSource implements ProductDataSource {
    private List<Product> products;
    private static final String CSV_FILE_PATH = "data/products.csv";
    
    public CsvProductDataSource() {
        this.products = loadProductsFromCsv();
    }
    
    @Override
    public List<Product> getAllProducts() {
        return products;
    }
    
    @Override
    public Product getProductById(String id) {
        int rowId = Integer.parseInt(id);
        if (rowId >= 0 && rowId < products.size()) {
            return products.get(rowId);
        }
        return null;
    }
    
    @Override
    public Product saveProduct(Product product) {
        // Assign the next row number as ID
        product.setId(String.valueOf(products.size()));
        products.add(product);
        saveProductsToCsv(products);
        return product;
    }
    
    @Override
    public Product updateProduct(Product product) {
        int rowId = Integer.parseInt(product.getId());
        if (rowId >= 0 && rowId < products.size()) {
            products.set(rowId, product);
            saveProductsToCsv(products);
            return product;
        }
        return null;
    }
    
    @Override
    public void deleteProduct(String id) {
        int rowId = Integer.parseInt(id);
        if (rowId >= 0 && rowId < products.size()) {
            products.remove(rowId);
            
            // Update IDs for all subsequent products
            for (int i = rowId; i < products.size(); i++) {
                products.get(i).setId(String.valueOf(i));
            }
            
            saveProductsToCsv(products);
        }
    }
    
    /**
     * Load products from the CSV file.
     *
     * @return List of products
     */
    private List<Product> loadProductsFromCsv() {
        List<Product> productList = new ArrayList<>();
        
        try {
            ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);
            Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
            
            int rowIndex = 0;
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
                // Set the row number as ID
                product.setId(String.valueOf(rowIndex));
                productList.add(product);
                rowIndex++;
            }
            
            csvParser.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return productList;
    }
    
    /**
     * Save products to the CSV file.
     *
     * @param products List of products to save
     */
    private void saveProductsToCsv(List<Product> products) {
        try {
            // Get the path to the CSV file
            Path path = Paths.get("src/main/resources/" + CSV_FILE_PATH);
            
            // Create writer
            BufferedWriter writer = Files.newBufferedWriter(path);
            
            // Create CSV printer
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("name", "category", "brand", "minimumSellingPrice", 
                               "maximumSellingPrice", "stockAvailability", "photoUrl", "barcode"));
            
            // Write all products (without the ID field)
            for (Product product : products) {
                csvPrinter.printRecord(
                        product.getName(),
                        product.getCategory(),
                        product.getBrand(),
                        product.getMinimumSellingPrice(),
                        product.getMaximumSellingPrice(),
                        product.getStockAvailability(),
                        product.getPhotoUrl(),
                        product.getBarcode()
                );
            }
            
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
