package com.example.springBootLearn.controller;

import com.example.springBootLearn.model.Product;
import com.example.springBootLearn.service.PriceListService;
import com.example.springBootLearn.service.ProductDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for the Firestore price list manager.
 */
@Controller
@RequestMapping("/firestore-price-list")
public class FirestorePriceListController {

    private final PriceListService priceListService;
    
    @Autowired
    public FirestorePriceListController(
            PriceListService priceListService,
            @Qualifier("firestoreProductDataSource") ProductDataSource firestoreDataSource) {
        this.priceListService = priceListService;
        // Switch to Firestore data source
        this.priceListService.setDataSource(firestoreDataSource);
    }

    /**
     * Display the price list page.
     *
     * @param model The model to add attributes to
     * @param isAdmin Flag to indicate if admin mode is enabled
     * @return The view name
     */
    @GetMapping
    public String getPriceListPage(Model model, @RequestParam(required = false) String admin) {
        model.addAttribute("products", priceListService.getAllProducts());
        model.addAttribute("categories", priceListService.getUniqueCategories());
        model.addAttribute("brands", priceListService.getUniqueBrands());
        model.addAttribute("isFirestore", true);
        model.addAttribute("isAdmin", "azsxazsx".equals(admin));
        return "price-list/index";
    }

    /**
     * API endpoint for searching products.
     *
     * @param query The search query
     * @return List of products matching the query
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(priceListService.searchProducts(query));
    }

    /**
     * API endpoint for autocomplete suggestions.
     *
     * @param term The search term
     * @return List of suggestions
     */
    @GetMapping("/autocomplete")
    @ResponseBody
    public ResponseEntity<List<String>> getAutocompleteSuggestions(@RequestParam String term) {
        String lowerCaseTerm = term.toLowerCase();
        
        // Get product names matching the term
        List<String> productNameSuggestions = priceListService.getProductNames().stream()
                .filter(name -> name.toLowerCase().contains(lowerCaseTerm))
                .collect(Collectors.toList());
        
        // Get brand names matching the term
        List<String> brandSuggestions = priceListService.getUniqueBrands().stream()
                .filter(brand -> brand.toLowerCase().contains(lowerCaseTerm))
                .collect(Collectors.toList());
        
        // Get category names matching the term
        List<String> categorySuggestions = priceListService.getUniqueCategories().stream()
                .filter(category -> category.toLowerCase().contains(lowerCaseTerm))
                .collect(Collectors.toList());
        
        // Get barcodes matching the term
        List<String> barcodeSuggestions = priceListService.getAllProducts().stream()
                .map(Product::getBarcode)
                .filter(barcode -> barcode != null && !barcode.isEmpty() && barcode.toLowerCase().contains(lowerCaseTerm))
                .distinct()
                .collect(Collectors.toList());
        
        // Combine all suggestions, prioritizing product names first, then barcodes
        List<String> allSuggestions = Stream.concat(
                Stream.concat(
                    Stream.concat(productNameSuggestions.stream(), barcodeSuggestions.stream()),
                    brandSuggestions.stream()
                ),
                categorySuggestions.stream()
            )
            .distinct()
            .limit(10) // Limit to 10 suggestions for better UX
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(allSuggestions);
    }
    
    /**
     * API endpoint for getting a product by ID.
     *
     * @param id The product ID
     * @return The product
     */
    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product = priceListService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * API endpoint for adding a new product.
     * Only accessible with admin query parameter.
     *
     * @param product The product to add
     * @param admin Admin access check
     * @return The added product
     */
    @PostMapping("/products")
    @ResponseBody
    public ResponseEntity<?> addProduct(@RequestBody Product product, @RequestParam(required = false) String admin) {
        if (!"azsxazsx".equals(admin)) {
            return ResponseEntity.status(403).body("Admin access required");
        }
        
        return ResponseEntity.ok(priceListService.saveProduct(product));
    }
    
    /**
     * API endpoint for updating a product.
     * Only accessible with admin query parameter.
     *
     * @param id The product ID
     * @param product The updated product
     * @param admin Admin access check
     * @return The updated product
     */
    @PutMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<?> updateProduct(
            @PathVariable String id, 
            @RequestBody Product product,
            @RequestParam(required = false) String admin) {
        if (!"azsxazsx".equals(admin)) {
            return ResponseEntity.status(403).body("Admin access required");
        }
        
        product.setId(id);
        Product updatedProduct = priceListService.updateProduct(product);
        
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * API endpoint for deleting a product.
     * Only accessible with admin query parameter.
     *
     * @param id The product ID
     * @param admin Admin access check
     * @return No content response
     */
    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@PathVariable String id, @RequestParam(required = false) String admin) {
        if (!"azsxazsx".equals(admin)) {
            return ResponseEntity.status(403).body("Admin access required");
        }
        
        priceListService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
