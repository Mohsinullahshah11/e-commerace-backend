package com.ecommerce.service;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.StoreRegisterRequest;
import com.ecommerce.model.Product;
import com.ecommerce.model.Store;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    public Store register(StoreRegisterRequest req) {
        if (storeRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered as a store");
        }
        Store store = new Store(req.getName(), req.getEmail(), req.getPassword());
        store.setStoreName(req.getStoreName());
        store.setStoreDescription(req.getStoreDescription());
        store.setStatus("ACTIVE");
        return storeRepository.save(store);
    }

    public Store login(LoginRequest req) {
        Store store = storeRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!store.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        if ("SUSPENDED".equals(store.getStatus())) {
            throw new RuntimeException("Your store has been suspended. Please contact admin.");
        }
        return store;
    }

    public Product addProduct(Long storeId, ProductRequest req) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        if ("SUSPENDED".equals(store.getStatus())) {
            throw new RuntimeException("Your store is suspended and cannot add products.");
        }
        Product product = new Product(
                req.getName(), req.getDescription(), req.getPrice(),
                req.getStockQuantity(), req.getImageUrl(), req.getCategory()
        );
        product.setSalePrice(req.getSalePrice());
        product.setStore(store);
        return productRepository.save(product);
    }

    public Product updateProduct(Long storeId, Long productId, ProductRequest req) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStore() == null || !product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("You can only edit your own products");
        }
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setSalePrice(req.getSalePrice());
        product.setStockQuantity(req.getStockQuantity());
        product.setImageUrl(req.getImageUrl());
        product.setCategory(req.getCategory());
        return productRepository.save(product);
    }

    public void deleteProduct(Long storeId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStore() == null || !product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("You can only delete your own products");
        }
        productRepository.delete(product);
    }

    public List<Product> getStoreProducts(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return productRepository.findByStore_Id(storeId);
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found"));
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store updateStatus(Long id, String status) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        store.setStatus(status);
        return storeRepository.save(store);
    }
}
