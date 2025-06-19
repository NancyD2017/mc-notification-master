package skillbox.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skillbox.notification.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiverId(UUID receiverId);
    Long countByReceiverIdAndIsReadedFalse(UUID receiverId);
    List<Notification> findByContentAndReceiverId(String content, UUID receiverId);
}
