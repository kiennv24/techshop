package backend.service.impl;

import backend.dto.response.NotificationResponse;
import backend.entity.Notification;
import backend.entity.User;
import backend.exception.ResourceNotFoundException;
import backend.repository.NotificationRepository;
import backend.repository.UserRepository;
import backend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(n -> new NotificationResponse(
            n.getId(),
            n.getTitle(),
            n.getMessage(),
            n.isRead(),
            n.getCreatedAt()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notification.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian khi người dùng nhấn xem
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
}