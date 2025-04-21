package com.example.pricelist.controller;

import com.example.pricelist.model.CartItemRequest;
import com.example.pricelist.model.Product;
import com.example.pricelist.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for the price list manager.
 */
@Controller
@RequestMapping("/price-list")
public class PriceListController {

    private final PriceListService priceListService;

    @Autowired
    public PriceListController(PriceListService priceListService) {
        this.priceListService = priceListService;
    }

    /**
     * Display the price list page.
     *
     * @return ModelAndView containing the view name and model attributes
     */
    @GetMapping
    public ModelAndView getPriceListPage() {
        ModelAndView modelAndView = new ModelAndView("price-list/index");
        modelAndView.addObject("products", priceListService.getAllProducts());
        modelAndView.addObject("categories", priceListService.getUniqueCategories());
        modelAndView.addObject("brands", priceListService.getUniqueBrands());
        modelAndView.addObject("cartItemRequest", new CartItemRequest());
        return modelAndView;
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
}
