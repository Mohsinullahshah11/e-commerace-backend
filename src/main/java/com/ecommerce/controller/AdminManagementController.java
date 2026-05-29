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
        List<Order> orders = orderRepository.findAll();

        // Revenue only from CONFIRMED orders (cancelled orders excluded)
        double totalRevenue = orders.stream()
                .filter(o -> "CONFIRMED".equals(o.getStatus()))
                .mapToDouble(Order::getTotalAmount).sum();

        long confirmedOrders = orders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
        long cancelledOrders = orders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();

        // Top 5 best-selling products — only from CONFIRMED orders
        List<OrderItem> allItems = orderItemRepository.findAll().stream()
                .filter(item -> "CONFIRMED".equals(item.getOrder().getStatus()))
                .collect(Collectors.toList());

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
        stats.put("totalRevenue",    totalRevenue);
        stats.put("totalOrders",     confirmedOrders);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("totalCustomers",  customerRepository.count());
        stats.put("totalStores",     storeRepository.count());
        stats.put("totalProducts",   productRepository.count());
        stats.put("topProducts",     topProducts);

        return ResponseEntity.ok(stats);
    }

    // ── All Orders ───────────────────────────────────────────────────────────

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<Map<String, Object>> result = orders.stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .map(o -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("orderId",        o.getId());
                    m.put("customerName",   o.getCustomer() != null ? o.getCustomer().getName() : "—");
                    m.put("customerEmail",  o.getCustomer() != null ? o.getCustomer().getEmail() : "—");
                    m.put("totalAmount",    o.getTotalAmount());
                    m.put("discountAmount", o.getDiscountAmount());
                    m.put("couponCode",     o.getCouponCode());
                    m.put("status",         o.getStatus());
                    m.put("orderDate",      o.getOrderDate());
                    m.put("itemCount",      o.getOrderItems().size());

                    // Include order items with product details
                    List<Map<String, Object>> items = o.getOrderItems().stream().map(item -> {
                        Map<String, Object> i = new LinkedHashMap<>();
                        i.put("productId",       item.getProduct().getId());
                        i.put("productName",     item.getProduct().getName());
                        i.put("productImage",    item.getProduct().getImageUrl());
                        i.put("category",        item.getProduct().getCategory());
                        i.put("quantity",        item.getQuantity());
                        i.put("priceAtPurchase", item.getPriceAtPurchase());
                        i.put("subtotal",        item.getPriceAtPurchase() * item.getQuantity());
                        return i;
                    }).collect(Collectors.toList());
                    m.put("items", items);

                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
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
