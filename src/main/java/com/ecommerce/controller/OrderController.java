package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> request) {
        try {
            Object customerIdRaw = request.get("customerId");
            if (customerIdRaw == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "customerId is required"));
            }
            Long customerId = ((Number) customerIdRaw).longValue();
            String couponCode = (String) request.get("couponCode");

            Order order = orderService.placeOrder(customerId, couponCode);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order placed successfully!");
            response.put("orderId", order.getId());
            response.put("totalAmount", order.getTotalAmount());
            response.put("discountAmount", order.getDiscountAmount());
            response.put("couponCode", order.getCouponCode());
            response.put("status", order.getStatus());
            response.put("orderDate", order.getOrderDate());
            response.put("itemCount", order.getOrderItems().size());

            if (order.getPayment() != null) {
                response.put("paymentStatus", order.getPayment().getStatus());
                response.put("paymentMethod", order.getPayment().getMethod());
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerOrders(@PathVariable Long customerId) {
        try {
            List<Order> orders = orderService.getCustomerOrders(customerId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, @RequestBody Map<String, Long> body) {
        try {
            Long customerId = body.get("customerId");
            Order order = orderService.cancelOrder(orderId, customerId);
            return ResponseEntity.ok(Map.of(
                "message", "Order cancelled successfully",
                "orderId", order.getId(),
                "status", order.getStatus()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
