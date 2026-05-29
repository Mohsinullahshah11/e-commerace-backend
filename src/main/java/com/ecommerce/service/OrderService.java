package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.ecommerce.repository.ProductRepository productRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Order cancelOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Order does not belong to this customer");
        }
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException("Only CONFIRMED orders can be cancelled");
        }
        for (OrderItem item : order.getOrderItems()) {
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
            productRepository.save(p);
        }
        order.setStatus("CANCELLED");
        Order saved = orderRepository.save(order);
        notificationService.create(customerId,
            "Your order #" + orderId + " has been cancelled. Stock has been restored.", "ORDER_CANCELLED");
        return saved;
    }

    @Transactional
    public Order placeOrder(Long customerId, String couponCode) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = cartService.getCart(customerId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Add items before placing an order.");
        }

        double subtotal = cart.getTotalAmount();

        // Apply coupon if provided
        double discountAmount = 0.0;
        String appliedCouponCode = null;
        Coupon coupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            coupon = couponService.validateCoupon(couponCode, subtotal);
            discountAmount = couponService.computeDiscount(coupon, subtotal);
            appliedCouponCode = coupon.getCode();
        }

        double total = Math.max(0, subtotal - discountAmount);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CONFIRMED");
        order.setTotalAmount(total);
        order.setDiscountAmount(discountAmount);
        order.setCouponCode(appliedCouponCode);
        Order savedOrder = orderRepository.save(order);

        // Validate stock first so we never partially fulfil an order
        for (CartItem cartItem : cart.getItems()) {
            Product p = cartItem.getProduct();
            if (p.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("\"" + p.getName() +
                        "\" only has " + p.getStockQuantity() + " left in stock.");
            }
        }

        // Create order items and deduct stock (use effectivePrice for price at purchase)
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product p = cartItem.getProduct();
            p.setStockQuantity(p.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(p);
            orderItems.add(orderItemRepository.save(
                    new OrderItem(savedOrder, p, cartItem.getQuantity(), p.getEffectivePrice())
            ));
        }
        savedOrder.setOrderItems(orderItems);

        if (coupon != null) couponService.markUsed(coupon);

        Payment payment = new Payment(savedOrder, total);
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        cartService.clearCart(customerId);

        Order finalOrder = orderRepository.findById(savedOrder.getId()).orElse(savedOrder);

        notificationService.create(customerId,
            "Order #" + finalOrder.getId() + " placed successfully! Total: $" + String.format("%.2f", total), "ORDER_PLACED");

        // Send order confirmation email (errors are caught inside EmailService)
        emailService.sendOrderConfirmationEmail(customer.getEmail(), customer.getName(), finalOrder);

        return finalOrder;
    }

    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomer_Id(customerId);
    }
}
