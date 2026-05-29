package com.ecommerce.service;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    // Returns customer's cart, creating one if it doesn't exist
    public Cart getCart(Long customerId) {
        return cartRepository.findByCustomer_Id(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new RuntimeException("Customer not found"));
                    Cart newCart = new Cart(customer);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItem(Long customerId, Long productId, int quantity) {
        Cart cart = getCart(customerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // If item already in cart, increase quantity
        CartItem existingItem = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(newItem);
        }

        return cartRepository.findByCustomer_Id(customerId).orElse(cart);
    }

    @Transactional
    public Cart removeItem(Long customerId, Long productId) {
        Cart cart = getCart(customerId);
        CartItem item = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartItemRepository.delete(item);
        return cartRepository.findByCustomer_Id(customerId).orElse(cart);
    }

    @Transactional
    public Cart updateQuantity(Long customerId, Long productId, int quantity) {
        Cart cart = getCart(customerId);
        CartItem item = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return cartRepository.findByCustomer_Id(customerId).orElse(cart);
    }

    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = cartRepository.findByCustomer_Id(customerId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }
}
