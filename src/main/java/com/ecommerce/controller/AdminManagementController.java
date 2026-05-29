package com.ecommerce.controller;

import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminManagementController {

    @Autowired private StoreService storeService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StoreRepository storeRepository;

    // ── Dashboard Stats ──────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        // Basic counts
        List<Order> orders = orderRepository.findAll();
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();

        // Top 5 best-selling products from order items
        List<OrderItem> allItems = orderItemRepository.findAll();
        Map<Long, Long>    salesMap   = new HashMap<>();
        Map<Long, Product> productMap = new HashMap<>();
        for (OrderItem item : allItems) {
            Long pid = item.getProduct().getId();
            salesMap.merge(pid, (long) item.getQuantity(), Long::sum);
            productMap.put(pid, item.getProduct());
        }

        List<Map<String, Object>> topProducts = salesMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Product p = productMap.get(e.getKey());
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("productId",  p.getId());
                    m.put("name",       p.getName());
                    m.put("category",   p.getCategory());
                    m.put("imageUrl",   p.getImageUrl());
                    m.put("price",      p.getPrice());
                    m.put("totalSold",  e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRevenue",   totalRevenue);
        stats.put("totalOrders",    (long) orders.size());
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalStores",    storeRepository.count());
        stats.put("totalProducts",  productRepository.count());
        stats.put("topProducts",    topProducts);

        return ResponseEntity.ok(stats);
    }

    // ── Stores ───────────────────────────────────────────────────────────────

    @GetMapping("/stores")
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @PutMapping("/stores/{id}/status")
    public ResponseEntity<?> updateStoreStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            if (!status.equals("ACTIVE") && !status.equals("SUSPENDED")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Status must be ACTIVE or SUSPENDED"));
            }
            return ResponseEntity.ok(storeService.updateStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ── Customers ────────────────────────────────────────────────────────────

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            customerRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Customer deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
