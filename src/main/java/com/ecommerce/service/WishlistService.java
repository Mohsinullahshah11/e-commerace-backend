package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.WishlistItem;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<WishlistItem> getWishlist(Long customerId) {
        return wishlistRepository.findByCustomerId(customerId);
    }

    public WishlistItem addToWishlist(Long customerId, Long productId) {
        if (wishlistRepository.existsByCustomerIdAndProduct_Id(customerId, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return wishlistRepository.save(new WishlistItem(customerId, product));
    }

    public void removeFromWishlist(Long customerId, Long productId) {
        WishlistItem item = wishlistRepository.findByCustomerIdAndProduct_Id(customerId, productId)
                .orElseThrow(() -> new RuntimeException("Item not in wishlist"));
        wishlistRepository.delete(item);
    }

    public boolean isInWishlist(Long customerId, Long productId) {
        return wishlistRepository.existsByCustomerIdAndProduct_Id(customerId, productId);
    }
}
