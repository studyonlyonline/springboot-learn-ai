package com.example.pricelist.db;

import com.example.common.service.FirestoreInitializer;
import com.example.pricelist.model.Cart;
import com.example.pricelist.model.CartItem;
import com.example.pricelist.model.Product;
import com.example.pricelist.service.ProductDataSource;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Firestore implementation of CartRepository.
 * This implementation stores cart items embedded within the cart document.
 */
@Repository
public class FirestoreCartRepository implements CartRepository {
    private final Firestore db;
    private final ProductDataSource productDataSource;
    private final Cache<String, Cart> cartCache;
    
    private static final String CARTS_COLLECTION = "priceList_showroom_carts_collection";
    
    @Autowired
    public FirestoreCartRepository(
            FirestoreInitializer firestoreInitializer,
            @Qualifier("firestoreProductDataSource") ProductDataSource productDataSource) {
        this.db = firestoreInitializer.getDb();
        this.productDataSource = productDataSource;
        this.cartCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }
    
    @Override
    public Cart getCartById(String id) {
        return cartCache.get(id, key -> {
            try {
                // Get cart document with embedded items
                DocumentSnapshot cartDoc = db.collection(CARTS_COLLECTION).document(id).get().get();
                if (!cartDoc.exists()) {
                    return null;
                }
                
                // Cart items are already embedded in the document
                Cart cart = cartDoc.toObject(Cart.class);
                
                // Load product details for each cart item if needed
                if (cart != null && cart.getItems() != null) {
                    for (CartItem item : cart.getItems()) {
                        if (item.getProduct() == null && item.getProductId() != null) {
                            Product product = productDataSource.getProductById(item.getProductId());
                            item.setProduct(product);
                        }
                    }
                }
                
                return cart;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    
    @Override
    public Cart saveCart(Cart cart) {
        try {
            // If no ID is provided, generate one
            if (cart.getId() == null) {
                cart.setId(UUID.randomUUID().toString());
            }
            
            // Ensure all cart items have IDs
            for (CartItem item : cart.getItems()) {
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID().toString());
                }
                
                // Ensure cartId is set correctly
                item.setCartId(cart.getId());
            }
            
            // Save cart document with embedded items
            db.collection(CARTS_COLLECTION).document(cart.getId()).set(cart).get();
            
            // Update cache
            cartCache.put(cart.getId(), cart);
            
            return cart;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void deleteCart(String id) {
        try {
            // Delete cart document (items are embedded, so no need to delete them separately)
            db.collection(CARTS_COLLECTION).document(id).delete().get();
            
            // Remove from cache
            cartCache.invalidate(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public CartItem addItemToCart(String cartId, CartItem item) {
        try {
            // Get the cart
            Cart cart = getCartById(cartId);
            if (cart == null) {
                cart = new Cart(cartId);
            }
            
            // Set cart ID and generate item ID if not present
            item.setCartId(cartId);
            if (item.getId() == null) {
                item.setId(UUID.randomUUID().toString());
            }
            
            // Load product details if not present
            if (item.getProduct() == null && item.getProductId() != null) {
                Product product = productDataSource.getProductById(item.getProductId());
                item.setProduct(product);
            }
            
            // Check if item already exists in cart
            boolean itemExists = false;
            for (int i = 0; i < cart.getItems().size(); i++) {
                if (cart.getItems().get(i).getProductId().equals(item.getProductId())) {
                    cart.getItems().set(i, item);
                    itemExists = true;
                    break;
                }
            }
            
            // If item doesn't exist, add it
            if (!itemExists) {
                cart.getItems().add(item);
            }
            
            // Save the cart with the updated items
            saveCart(cart);
            
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void removeItemFromCart(String cartId, String itemId) {
        try {
            // Get the cart
            Cart cart = getCartById(cartId);
            if (cart != null) {
                // Remove the item
                cart.getItems().removeIf(item -> item.getId().equals(itemId));
                
                // Save the updated cart
                saveCart(cart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateCartItem(String cartId, CartItem item) {
        try {
            // Get the cart
            Cart cart = getCartById(cartId);
            if (cart != null) {
                // Ensure cart ID is set
                item.setCartId(cartId);
                
                // Find and update the item
                boolean itemFound = false;
                for (int i = 0; i < cart.getItems().size(); i++) {
                    if (cart.getItems().get(i).getId().equals(item.getId())) {
                        cart.getItems().set(i, item);
                        itemFound = true;
                        break;
                    }
                }
                
                // If item wasn't found but has an ID, add it
                if (!itemFound && item.getId() != null) {
                    cart.getItems().add(item);
                }
                
                // Save the updated cart
                saveCart(cart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
