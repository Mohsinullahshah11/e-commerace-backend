package com.ecommerce.repository;

import com.ecommerce.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByCustomerId(Long customerId);
    Optional<WishlistItem> findByCustomerIdAndProduct_Id(Long customerId, Long productId);
    boolean existsByCustomerIdAndProduct_Id(Long customerId, Long productId);
}
