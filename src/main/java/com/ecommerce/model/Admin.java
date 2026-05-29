package com.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Admin() {}

    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
