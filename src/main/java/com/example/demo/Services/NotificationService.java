package com.example.demo.Services;

import com.example.demo.Exceptions.BusinessException;
import com.example.demo.Model.DTOS.Response.NotificationResponse;
import com.example.demo.Model.Entities.NotificationEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Model.Enums.ErrorCode;
import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.Repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void createNotification(String email, String cartId, String message) {
        // notificacion local
        saveToDataBase(email, cartId, message);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // notificacion por correo
        sendEmailWithTemplate(email, user.getName(), cartId);
    }

    public void saveToDataBase(String email, String cartId, String message) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setRelatedOrderId(cartId);
        notification.setMessage(message);
        notification.setCreatedAt(Instant.now());
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public void sendEmailWithTemplate(String to, String nombre, String cartId) {
        UserEntity user = userRepository.findByEmail(to)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isNotificationsEnabled()) {
            System.out.println("Usuario deshabilitó las notificaciones. No se envía correo.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Preparar variables para el HTML
            Context context = new Context();
            context.setVariable("nombreCliente", nombre);
            context.setVariable("cartId", cartId);
            context.setVariable("urlPedido", "https://puertocolores.com/my-orders/" + cartId);

            // Procesar el HTML
            String htmlContent = templateEngine.process("order-ready", context);

            helper.setTo(to);
            helper.setSubject("Tu pedido en Puerto Colores está listo");
            helper.setText(htmlContent, true); // El 'true' indica que es HTML
            helper.setFrom("notificaciones@puertocolores.com");

            mailSender.send(message);
        }catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadForUser(String email) {
        return notificationRepository.findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(email)
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getRelatedOrderId(), n.getMessage(), n.getCreatedAt()))
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
