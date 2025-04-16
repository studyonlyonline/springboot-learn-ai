package com.example.springBootLearn.service;

import com.example.common.service.FirestoreInitializer;
import com.example.springBootLearn.model.Product;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of ProductDataSource that reads/writes from/to Firestore.
 * Uses Caffeine cache to improve performance.
 */
@Component("firestoreProductDataSource")
public class FirestoreProductDataSource implements ProductDataSource {
    private final Firestore db;
    private final Cache<String, List<Product>> productCache;
    private static final String COLLECTION_NAME = "priceList_showroom_products_collection";
    
    @Autowired
    public FirestoreProductDataSource(FirestoreInitializer firestoreInitializer) {
        this.db = firestoreInitializer.getDb();
        this.productCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
        
        // Initialize cache with all products
        initializeCache();
    }
    
    /**
     * Initialize the cache with all products from Firestore.
     */
    private void initializeCache() {
        try {
            List<Product> products = fetchAllProductsFromFirestore();
            productCache.put("allProducts", products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fetch all products from Firestore.
     *
     * @return List of products
     * @throws ExecutionException If an error occurs during execution
     * @throws InterruptedException If the operation is interrupted
     */
    private List<Product> fetchAllProductsFromFirestore() throws ExecutionException, InterruptedException {
        List<Product> products = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        for (QueryDocumentSnapshot document : documents) {
            Product product = document.toObject(Product.class);
            product.setId(document.getId());
            products.add(product);
        }
        
        return products;
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productCache.get("allProducts", key -> {
            try {
                return fetchAllProductsFromFirestore();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public Product getProductById(String id) {
        try {
            DocumentSnapshot document = db.collection(COLLECTION_NAME).document(id).get().get();
            if (document.exists()) {
                Product product = document.toObject(Product.class);
                if (product != null) {
                    product.setId(document.getId());
                }
                return product;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Product saveProduct(Product product) {
        try {
            // Generate ID if not present
            if (product.getId() == null) {
                product.setId(UUID.randomUUID().toString());
            }
            
            // Save to Firestore
            db.collection(COLLECTION_NAME).document(product.getId()).set(product).get();
            
            // Update cache
            List<Product> products = getAllProducts();
            products.add(product);
            productCache.put("allProducts", products);
            
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Product updateProduct(Product product) {
        try {
            // Update in Firestore
            db.collection(COLLECTION_NAME).document(product.getId()).set(product).get();
            
            // Update cache
            List<Product> products = getAllProducts();
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(product.getId())) {
                    products.set(i, product);
                    break;
                }
            }
            productCache.put("allProducts", products);
            
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void deleteProduct(String id) {
        try {
            // Delete from Firestore
            db.collection(COLLECTION_NAME).document(id).delete().get();
            
            // Update cache
            List<Product> products = getAllProducts();
            products.removeIf(p -> p.getId().equals(id));
            productCache.put("allProducts", products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
