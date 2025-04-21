package com.example.pricelist.service;

import com.example.pricelist.db.CartRepository;
import com.example.pricelist.model.Cart;
import com.example.pricelist.model.CartItem;
import com.example.pricelist.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for managing shopping carts.
 */
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductDataSource productDataSource;
    
    @Autowired
    public CartService(CartRepository cartRepository, 
                       @Qualifier("firestoreProductDataSource") ProductDataSource productDataSource) {
        this.cartRepository = cartRepository;
        this.productDataSource = productDataSource;
    }
    
    /**
     * Get a cart by ID, or create a new one if it doesn't exist.
     *
     * @param cartId The cart ID, or null to create a new cart
     * @return The cart
     */
    public Cart getOrCreateCart(String cartId) {
        if (cartId != null) {
            Cart cart = cartRepository.getCartById(cartId);
            if (cart != null) {
                return cart;
            }
        }
        
        // Create a new cart
        String newCartId = UUID.randomUUID().toString();
        System.out.println("new cart d created " + newCartId);
        Cart cart = new Cart(newCartId);
        return cartRepository.saveCart(cart);
    }
    
    /**
     * Add a product to a cart.
     *
     * @param cartId The cart ID
     * @param productId The product ID
     * @param quantity The quantity to add
     * @param sellingPrice The selling price
     * @return The updated cart
     */
    public Cart addToCart(String cartId, String productId, int quantity, double sellingPrice) {
        Cart cart = getOrCreateCart(cartId);
        Product product = productDataSource.getProductById(productId);
        
        System.out.println("get cart id " + cart.getId());

        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        
        // Validate selling price is within range
        if (sellingPrice < product.getMinimumSellingPrice() || sellingPrice > product.getMaximumSellingPrice()) {
            throw new IllegalArgumentException("Selling price must be between " + 
                    product.getMinimumSellingPrice() + " and " + product.getMaximumSellingPrice());
        }
        
        // Validate quantity is available
        if (quantity > product.getStockAvailability()) {
            throw new IllegalArgumentException("Not enough stock available. Only " + 
                    product.getStockAvailability() + " units available.");
        }
        
        // Add to cart
        cart.addItem(product, quantity, sellingPrice);
        
        // Save cart
        return cartRepository.saveCart(cart);
    }
    
    /**
     * Update the quantity of an item in the cart.
     *
     * @param cartId The cart ID
     * @param productId The product ID
     * @param quantity The new quantity
     * @return The updated cart
     */
    public Cart updateCartItem(String cartId, String productId, int quantity) {
        Cart cart = getOrCreateCart(cartId);
        
        // Find the cart item
        CartItem itemToUpdate = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                itemToUpdate = item;
                break;
            }
        }
        
        if (itemToUpdate == null) {
            throw new IllegalArgumentException("Product not in cart: " + productId);
        }
        
        // Validate quantity is available
        Product product = productDataSource.getProductById(productId);
        if (product != null && quantity > product.getStockAvailability()) {
            throw new IllegalArgumentException("Not enough stock available. Only " + 
                    product.getStockAvailability() + " units available.");
        }
        
        // Update quantity
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cart.removeItem(productId);
        } else {
            cart.updateItemQuantity(productId, quantity);
        }
        
        // Save cart
        return cartRepository.saveCart(cart);
    }
    
    /**
     * Remove an item from the cart.
     *
     * @param cartId The cart ID
     * @param productId The product ID
     * @return The updated cart
     */
    public Cart removeFromCart(String cartId, String productId) {
        Cart cart = getOrCreateCart(cartId);
        
        // Remove item
        cart.removeItem(productId);
        
        // Save cart
        return cartRepository.saveCart(cart);
    }
    
    /**
     * Clear all items from the cart.
     *
     * @param cartId The cart ID
     * @return The updated cart
     */
    public Cart clearCart(String cartId) {
        Cart cart = getOrCreateCart(cartId);
        
        // Clear cart
        cart.clear();
        
        // Save cart
        return cartRepository.saveCart(cart);
    }
}
