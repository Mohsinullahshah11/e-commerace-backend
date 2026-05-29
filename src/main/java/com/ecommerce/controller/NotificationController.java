package com.ecommerce.controller;

import com.ecommerce.model.Notification;
import com.ecommerce.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{customerId}")
    public List<Notification> getAll(@PathVariable Long customerId) {
        return notificationService.getForCustomer(customerId);
    }

    @GetMapping("/{customerId}/unread-count")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long customerId) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(customerId)));
    }

    @PutMapping("/{customerId}/mark-read")
    public ResponseEntity<?> markAllRead(@PathVariable Long customerId) {
        notificationService.markAllRead(customerId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
