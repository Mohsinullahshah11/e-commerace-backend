package com.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private double discountPercent;
    private double minOrderAmount;
    private int maxUses; // -1 = unlimited
    private int usedCount;
    private boolean active;

    public Coupon() {}

    public Coupon(String code, double discountPercent, double minOrderAmount, int maxUses) {
        this.code = code.toUpperCase();
        this.discountPercent = discountPercent;
        this.minOrderAmount = minOrderAmount;
        this.maxUses = maxUses;
        this.usedCount = 0;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code.toUpperCase(); }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
