package com.ecommerce.controller;

import com.ecommerce.model.WishlistItem;
import com.ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/{customerId}")
    public List<WishlistItem> getWishlist(@PathVariable Long customerId) {
        return wishlistService.getWishlist(customerId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Map<String, Long> body) {
        try {
            WishlistItem item = wishlistService.addToWishlist(body.get("customerId"), body.get("productId"));
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{customerId}/{productId}")
    public ResponseEntity<?> remove(@PathVariable Long customerId, @PathVariable Long productId) {
        try {
            wishlistService.removeFromWishlist(customerId, productId);
            return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/check/{customerId}/{productId}")
    public ResponseEntity<?> check(@PathVariable Long customerId, @PathVariable Long productId) {
        return ResponseEntity.ok(Map.of("inWishlist", wishlistService.isInWishlist(customerId, productId)));
    }
}
