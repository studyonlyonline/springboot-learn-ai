package com.example.pricelist.controller;

import com.example.pricelist.model.Cart;
import com.example.pricelist.model.Order;
import com.example.pricelist.service.CartService;
import com.example.pricelist.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for checkout operations.
 */
@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private static final String CART_ID_SESSION_ATTRIBUTE = "cartId";
    
    private final CartService cartService;
    private final OrderService orderService;
    
    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }
    
    /**
     * Display the checkout page.
     *
     * @param model The model to add attributes to
     * @param session The HTTP session
     * @return The view name
     */
    @GetMapping
    public String getCheckoutPage(Model model, HttpSession session) {
        String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
        Cart cart = cartService.getOrCreateCart(cartId);
        
        // Check if cart is empty
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cart", cart);
        return "price-list/checkout";
    }
    
    /**
     * Place an order.
     *
     * @param customerName The customer name
     * @param customerContact The customer contact information
     * @param paymentMethod The payment method
     * @param session The HTTP session
     * @return The created order
     */
    @PostMapping("/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(
            @RequestParam String customerName,
            @RequestParam String customerContact,
            @RequestParam String paymentMethod,
            HttpSession session) {
        try {
            String cartId = (String) session.getAttribute(CART_ID_SESSION_ATTRIBUTE);
            
            // Create order
            Order order = orderService.createOrderFromCart(cartId, customerName, customerContact, paymentMethod);
            
            // Clear cart ID from session
            session.removeAttribute(CART_ID_SESSION_ATTRIBUTE);
            
            // Return order details
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("total", order.getTotal());
            response.put("status", order.getOrderStatus());
            response.put("paymentStatus", order.getPaymentStatus());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Complete payment for an order.
     *
     * @param orderId The order ID
     * @param transactionId The transaction ID (optional)
     * @return The updated order status
     */
    @PostMapping("/complete-payment")
    @ResponseBody
    public ResponseEntity<?> completePayment(
            @RequestParam String orderId,
            @RequestParam(required = false) String transactionId) {
        try {
            // Update payment status
            Order order = orderService.updatePaymentStatus(orderId, "COMPLETED", transactionId);
            
            // Return order details
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("status", order.getOrderStatus());
            response.put("paymentStatus", order.getPaymentStatus());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Cancel payment for an order.
     *
     * @param orderId The order ID
     * @param reason The cancellation reason (optional)
     * @return The updated order status
     */
    @PostMapping("/cancel-payment")
    @ResponseBody
    public ResponseEntity<?> cancelPayment(
            @RequestParam String orderId,
            @RequestParam(required = false) String reason) {
        try {
            // Update payment status
            Order order = orderService.updatePaymentStatus(orderId, "FAILED", reason);
            
            // Return order details
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("status", order.getOrderStatus());
            response.put("paymentStatus", order.getPaymentStatus());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
