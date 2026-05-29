package com.ecommerce.controller;

import com.ecommerce.dto.CartRequest;
import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request) {
        try {
            int qty = request.getQuantity() > 0 ? request.getQuantity() : 1;
            Cart cart = cartService.addItem(request.getCustomerId(), request.getProductId(), qty);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCart(@PathVariable Long customerId) {
        try {
            Cart cart = cartService.getCart(customerId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody CartRequest request) {
        try {
            Cart cart = cartService.updateQuantity(request.getCustomerId(), request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/remove/{customerId}/{productId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long customerId,
            @PathVariable Long productId) {
        try {
            Cart cart = cartService.removeItem(customerId, productId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<?> clearCart(@PathVariable Long customerId) {
        try {
            cartService.clearCart(customerId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
