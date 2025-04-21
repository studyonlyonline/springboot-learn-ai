package com.example.pricelist.db;

import com.example.pricelist.model.Cart;
import com.example.pricelist.model.CartItem;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Cart operations.
 */
@Repository
public interface CartRepository {
    /**
     * Get a cart by ID.
     *
     * @param id The cart ID
     * @return The cart, or null if not found
     */
    Cart getCartById(String id);
    
    /**
     * Save a cart.
     *
     * @param cart The cart to save
     * @return The saved cart
     */
    Cart saveCart(Cart cart);
    
    /**
     * Delete a cart.
     *
     * @param id The cart ID
     */
    void deleteCart(String id);
    
    /**
     * Add an item to a cart.
     *
     * @param cartId The cart ID
     * @param item The item to add
     * @return The added cart item
     */
    CartItem addItemToCart(String cartId, CartItem item);
    
    /**
     * Remove an item from a cart.
     *
     * @param cartId The cart ID
     * @param itemId The item ID
     */
    void removeItemFromCart(String cartId, String itemId);
    
    /**
     * Update a cart item.
     *
     * @param cartId The cart ID
     * @param item The updated item
     */
    void updateCartItem(String cartId, CartItem item);
}
