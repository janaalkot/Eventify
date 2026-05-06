package com.evently.notification_service.service;

import com.evently.common.security.AuthenticatedUser;
import com.evently.notification_service.dto.NotificationRequest;
import com.evently.notification_service.entity.Notification;
import com.evently.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(NotificationRequest request, AuthenticatedUser authenticatedUser) {
        if (!authenticatedUser.isAdmin() && !authenticatedUser.id().equals(request.getUserId())) {
            throw new IllegalArgumentException("You can only create notifications for your own account");
        }

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle().trim());
        notification.setMessage(request.getMessage().trim());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setReadStatus(Boolean.TRUE.equals(request.getReadStatus()));
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId, AuthenticatedUser authenticatedUser) {
        if (!authenticatedUser.isAdmin() && !authenticatedUser.id().equals(userId)) {
            throw new IllegalArgumentException("You can only view your own notifications");
        }

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
