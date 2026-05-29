package com.ecommerce.controller;

import com.ecommerce.model.Coupon;
import com.ecommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // Customer: validate a coupon before applying
    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody Map<String, Object> body) {
        try {
            String code = (String) body.get("code");
            double total = ((Number) body.get("orderTotal")).doubleValue();
            Coupon coupon = couponService.validateCoupon(code, total);
            double discount = couponService.computeDiscount(coupon, total);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "code", coupon.getCode(),
                "discountPercent", coupon.getDiscountPercent(),
                "discountAmount", discount
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Admin: list all coupons
    @GetMapping
    public List<Coupon> getAll() { return couponService.getAllCoupons(); }

    // Admin: create coupon
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String code = (String) body.get("code");
            double discountPercent = ((Number) body.get("discountPercent")).doubleValue();
            double minOrderAmount = body.containsKey("minOrderAmount")
                    ? ((Number) body.get("minOrderAmount")).doubleValue() : 0.0;
            int maxUses = body.containsKey("maxUses") ? ((Number) body.get("maxUses")).intValue() : -1;
            return ResponseEntity.ok(couponService.createCoupon(code, discountPercent, minOrderAmount, maxUses));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Admin: toggle coupon active/inactive
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(couponService.toggleActive(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Admin: delete coupon
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(Map.of("message", "Coupon deleted"));
    }
}
