package it.unicam.cs.ids2425.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadIsFalse(Long recipientId);

    List<Notification> findByRecipientIdAndReadIsFalse(Long recipientId);

    Optional<Notification> findByIdAndRecipientId(UUID id, Long recipientId);
}