package com.ecommerce.service;

import com.ecommerce.model.Notification;
import com.ecommerce.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void create(Long customerId, String message, String type) {
        notificationRepository.save(new Notification(customerId, message, type));
    }

    public List<Notification> getForCustomer(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public long getUnreadCount(Long customerId) {
        return notificationRepository.countByCustomerIdAndReadFalse(customerId);
    }

    public void markAllRead(Long customerId) {
        List<Notification> all = notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        all.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(all);
    }
}
