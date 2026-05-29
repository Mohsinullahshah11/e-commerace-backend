package com.ecommerce.controller;

import com.ecommerce.model.Customer;
import com.ecommerce.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return customerRepository.findById(id)
                .<ResponseEntity<?>>map(c -> ResponseEntity.ok(Map.of(
                        "id", c.getId(),
                        "name", c.getName(),
                        "email", c.getEmail(),
                        "emailVerified", c.isEmailVerified()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateName(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Customer c = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            String name = body.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name cannot be empty"));
            }
            c.setName(name.trim());
            customerRepository.save(c);
            return ResponseEntity.ok(Map.of("id", c.getId(), "name", c.getName(), "email", c.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Customer c = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");
            if (!c.getPassword().equals(oldPassword)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Current password is incorrect"));
            }
            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "New password must be at least 6 characters"));
            }
            c.setPassword(newPassword);
            customerRepository.save(c);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
