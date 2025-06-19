package skillbox.notification.dto.events;

import lombok.Data;
import skillbox.notification.model.MicroServiceName;
import skillbox.notification.model.NotificationType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class NotificationEvent {
    private UUID id;
    private OffsetDateTime sentTime;
    private UUID authorId;
    private UUID receiverId;
    private List<UUID> receiverIds;
    private UUID eventId;
    private String content;
    private MicroServiceName serviceName;
    private NotificationType notificationType;
    private Boolean isRead;
    private String email;
}
