package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.StoreRegisterRequest;
import com.ecommerce.model.Product;
import com.ecommerce.model.Store;
import com.ecommerce.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody StoreRegisterRequest req) {
        try {
            Store store = storeService.register(req);
            Map<String, Object> res = new HashMap<>();
            res.put("storeId", store.getId());
            res.put("storeName", store.getStoreName());
            res.put("name", store.getName());
            res.put("email", store.getEmail());
            res.put("status", store.getStatus());
            res.put("role", "STORE");
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Store store = storeService.login(req);
            Map<String, Object> res = new HashMap<>();
            res.put("storeId", store.getId());
            res.put("storeName", store.getStoreName());
            res.put("name", store.getName());
            res.put("email", store.getEmail());
            res.put("status", store.getStatus());
            res.put("role", "STORE");
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{storeId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long storeId, @RequestBody Map<String, String> body) {
        try {
            Store store = storeService.updateProfile(storeId, body.get("storeName"), body.get("storeDescription"), body.get("logoUrl"));
            Map<String, Object> res = new HashMap<>();
            res.put("storeId", store.getId());
            res.put("storeName", store.getStoreName());
            res.put("storeDescription", store.getStoreDescription());
            res.put("logoUrl", store.getLogoUrl());
            res.put("status", store.getStatus());
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{storeId}/info")
    public ResponseEntity<?> getStoreInfo(@PathVariable Long storeId) {
        try {
            Store store = storeService.getStoreById(storeId);
            Map<String, Object> res = new HashMap<>();
            res.put("storeId", store.getId());
            res.put("storeName", store.getStoreName());
            res.put("storeDescription", store.getStoreDescription());
            res.put("status", store.getStatus());
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{storeId}/products")
    public ResponseEntity<?> getProducts(@PathVariable Long storeId) {
        try {
            List<Product> products = storeService.getStoreProducts(storeId);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{storeId}/products")
    public ResponseEntity<?> addProduct(@PathVariable Long storeId, @RequestBody ProductRequest req) {
        try {
            Product product = storeService.addProduct(storeId, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{storeId}/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long storeId, @PathVariable Long productId,
                                            @RequestBody ProductRequest req) {
        try {
            Product product = storeService.updateProduct(storeId, productId, req);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{storeId}/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long storeId, @PathVariable Long productId) {
        try {
            storeService.deleteProduct(storeId, productId);
            return ResponseEntity.ok(Map.of("message", "Product deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
