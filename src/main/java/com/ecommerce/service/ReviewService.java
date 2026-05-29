package com.ecommerce.service;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public Review addReview(ReviewRequest req) {
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        if (reviewRepository.existsByCustomer_IdAndProduct_Id(req.getCustomerId(), req.getProductId())) {
            throw new RuntimeException("You have already reviewed this product");
        }
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return reviewRepository.save(new Review(customer, product, req.getRating(), req.getComment()));
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }
}
