package com.example.pricelist.controller;

import com.example.pricelist.model.Cart;
import com.example.pricelist.model.CartItemRequest;
import com.example.pricelist.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for cart operations.
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    private static final String CART_ID_SESSION_ATTRIBUTE = "cartId";
    
    private final CartService cartService;
    
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * Display the cart page.
     *
     * @param model The model to add attributes to
     * @param session The HTTP session
     * @return The view name
     */
    @GetMapping
    public String getCartPage(Model model, HttpSession session) {
        String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
        Cart cart = cartService.getOrCreateCart(cartId);
        
        // Store cart ID in session
        session.setAttribute(CART_ID_SESSION_ATTRIBUTE, cart.getId());
        
        model.addAttribute("cart", cart);
        return "price-list/cart";
    }
    
    /**
     * Add a product to the cart.
     *
     * @param productId The product ID
     * @param quantity The quantity to add
     * @param sellingPrice The selling price
     * @param session The HTTP session
     * @return The updated cart
     */
    /**
     * Add a product to the cart using form submission.
     *
     * @param cartItemRequest The cart item request object
     * @param session The HTTP session
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to cart page or price list page
     */
    @PostMapping("/add")
    public String addToCart(
            @ModelAttribute CartItemRequest cartItemRequest,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Adding to cart with form: coming here" + cartItemRequest.getProductId());

            if (Objects.isNull(session.getAttribute(CART_ID_SESSION_ATTRIBUTE))) {
                System.out.println("null session");
            }

            System.out.println("session id " + session.getAttribute(CART_ID_SESSION_ATTRIBUTE));
            String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);

            System.out.println("cart id " + cartId);

            Cart cart = cartService.addToCart(
                cartId, 
                cartItemRequest.getProductId(), 
                cartItemRequest.getQuantity(), 
                cartItemRequest.getSellingPrice()
            );
            
            // Store cart ID in session
            session.setAttribute(CART_ID_SESSION_ATTRIBUTE, cart.getId());
            
            // Add success message
            redirectAttributes.addFlashAttribute("message", "Product added to cart successfully!");
            
            // Redirect to cart page
            return "redirect:/cart";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            System.out.println("coming here " + e.getMessage());

            // Add error message
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
            // Redirect back to price list
            return "redirect:/firestore-price-list";
        } catch (Exception ex) {
            System.out.println("catching exception " + ex);
            return "redirect:/firestore-price-list";
        }
    }
    
    /**
     * Add a product to the cart using AJAX (legacy method).
     *
     * @param productId The product ID
     * @param quantity The quantity to add
     * @param sellingPrice The selling price
     * @param session The HTTP session
     * @return The updated cart
     */
    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Cart> addToCartAjax(
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam double sellingPrice,
            HttpSession session) {
        try {
            System.out.println("Adding to cart with AJAX");
            String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);

            Cart cart = cartService.addToCart(cartId, productId, quantity, sellingPrice);
            
            // Store cart ID in session
            session.setAttribute(CART_ID_SESSION_ATTRIBUTE, cart.getId());
            
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            System.out.println("exception " + e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update the quantity of an item in the cart.
     *
     * @param productId The product ID
     * @param quantity The new quantity
     * @param session The HTTP session
     * @return The updated cart
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Cart> updateCartItem(
            @RequestParam String productId,
            @RequestParam int quantity,
            HttpSession session) {
        try {
            String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
            Cart cart = cartService.updateCartItem(cartId, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Remove an item from the cart.
     *
     * @param productId The product ID
     * @param session The HTTP session
     * @return The updated cart
     */
    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable String productId,
            HttpSession session) {
        try {
            String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
            Cart cart = cartService.removeFromCart(cartId, productId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Clear the cart.
     *
     * @param session The HTTP session
     * @return The updated cart
     */
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Cart> clearCart(HttpSession session) {
        try {
            String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
            Cart cart = cartService.clearCart(cartId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get the cart summary (item count and total).
     *
     * @param session The HTTP session
     * @return The cart summary
     */
    @GetMapping("/summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartSummary(HttpSession session) {
        String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
        Cart cart = cartService.getOrCreateCart(cartId);
        
        // Store cart ID in session
        session.setAttribute(CART_ID_SESSION_ATTRIBUTE, cart.getId());
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("itemCount", cart.getTotalItems());
        summary.put("total", cart.getTotal());
        
        return ResponseEntity.ok(summary);
    }
}
