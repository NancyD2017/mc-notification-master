package skillbox.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skillbox.notification.model.NotificationSettings;

import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {
    Optional<NotificationSettings> findByUserId(UUID userId);
}
