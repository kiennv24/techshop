package backend.services;

import backend.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(Long userId);
    void markAsRead(Long notificationId);
    void createNotification(Long userId, String title, String message); 
}