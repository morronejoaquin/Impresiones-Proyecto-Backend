package com.example.demo.Services;

import com.example.demo.Model.DTOS.Response.NotificationResponse;
import com.example.demo.Model.Entities.NotificationEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(String email, String message) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(Instant.now());
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadForUser(String email) {
        return notificationRepository.findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(email)
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(UUID id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(String email){
        List<NotificationEntity> unread = notificationRepository
                .findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(email);

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
