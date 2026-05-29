package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Boolean (wrapper) keeps the column nullable so ALTER TABLE works on existing DBs
    private Boolean emailVerified;

    @Column(unique = true)
    private String verificationToken;

    @JsonIgnore
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    public Customer() {}

    public Customer(String name, String email, String password) {
        super(name, email, password);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isEmailVerified() { return Boolean.TRUE.equals(emailVerified); }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
}
