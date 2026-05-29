package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Store inherits name/email/password from User — demonstrates OOP inheritance
@Entity
@Table(name = "stores")
public class Store extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;

    @Column(length = 500)
    private String storeDescription;

    private String logoUrl;

    // ACTIVE or SUSPENDED — controlled by admin
    private String status = "ACTIVE";

    @JsonIgnore
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Store() {}

    public Store(String name, String email, String password) {
        super(name, email, password);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
