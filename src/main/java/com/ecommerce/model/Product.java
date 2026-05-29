package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private double price;
    private int stockQuantity;
    private String imageUrl;
    private String category;
    private double salePrice; // 0 means no sale

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;

    @Transient
    private double avgRating;

    @Transient
    private int reviewCount;

    public Product() {}

    public Product(String name, String description, double price, int stockQuantity, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }

    public double getEffectivePrice() { return (salePrice > 0 && salePrice < price) ? salePrice : price; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public String getStoreName() {
        return store != null ? store.getStoreName() : "ShopEasy Official";
    }

    public Long getStoreId() {
        return store != null ? store.getId() : null;
    }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
}
