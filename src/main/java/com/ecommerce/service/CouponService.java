package com.ecommerce.service;

import com.ecommerce.model.Coupon;
import com.ecommerce.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon validateCoupon(String code, double orderTotal) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        if (!coupon.isActive()) throw new RuntimeException("Coupon is no longer active");
        if (coupon.getMaxUses() != -1 && coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new RuntimeException("Coupon usage limit has been reached");
        }
        if (orderTotal < coupon.getMinOrderAmount()) {
            throw new RuntimeException(
                "Minimum order amount of $" + String.format("%.2f", coupon.getMinOrderAmount()) + " required for this coupon"
            );
        }
        return coupon;
    }

    public double computeDiscount(Coupon coupon, double orderTotal) {
        return Math.round(orderTotal * (coupon.getDiscountPercent() / 100.0) * 100.0) / 100.0;
    }

    public void markUsed(Coupon coupon) {
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() { return couponRepository.findAll(); }

    public Coupon createCoupon(String code, double discountPercent, double minOrderAmount, int maxUses) {
        if (couponRepository.findByCode(code.toUpperCase()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }
        return couponRepository.save(new Coupon(code, discountPercent, minOrderAmount, maxUses));
    }

    public Coupon toggleActive(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setActive(!coupon.isActive());
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }
}
