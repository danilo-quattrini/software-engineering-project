package it.unicam.cs.ids2425.notification;

import it.unicam.cs.ids2425.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByRecipientIdAndReadIsFalse(userId);
    }

    public void markAsRead(UUID notificationId, Long userId) {
        notificationRepository.findByIdAndRecipientId(notificationId, userId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndReadIsFalse(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void notifyProductDecision(Product product, NotificationType type) {
        if (product.getUser() == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(product.getUser());
        notification.setType(type);
        notification.setProductId(product.getId());
        notification.setProductName(product.getName());

        String message = switch (type) {
            case PRODUCT_APPROVED -> "Il tuo prodotto '" + product.getName() + "' è stato approvato dal curatore.";
            case PRODUCT_REJECTED -> "Il tuo prodotto '" + product.getName() + "' è stato rifiutato dal curatore.";
        };

        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}