package com.ecommerce.service;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.model.Admin;
import com.ecommerce.model.Customer;
import com.ecommerce.repository.AdminRepository;
import com.ecommerce.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    public Customer registerCustomer(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        Customer customer = new Customer(request.getName(), request.getEmail(), request.getPassword());
        customer.setVerificationToken(UUID.randomUUID().toString());
        customer.setEmailVerified(false);
        Customer saved = customerRepository.save(customer);

        emailService.sendVerificationEmail(saved.getEmail(), saved.getName(), saved.getVerificationToken());
        return saved;
    }

    public Customer loginCustomer(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!customer.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        if (!customer.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before logging in. Check your inbox.");
        }
        return customer;
    }

    public Admin loginAdmin(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid admin credentials"));

        if (!admin.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid admin credentials");
        }
        return admin;
    }

    public void resendVerification(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
        if (customer.isEmailVerified()) {
            throw new RuntimeException("Email is already verified. Please login.");
        }
        if (customer.getVerificationToken() == null) {
            customer.setVerificationToken(UUID.randomUUID().toString());
            customerRepository.save(customer);
        }
        emailService.sendVerificationEmail(customer.getEmail(), customer.getName(), customer.getVerificationToken());
    }

    public void verifyEmail(String token) {
        Customer customer = customerRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification link."));

        if (customer.isEmailVerified()) {
            return; // already verified — idempotent
        }
        customer.setEmailVerified(true);
        customer.setVerificationToken(null); // consume the token
        customerRepository.save(customer);
    }
}
