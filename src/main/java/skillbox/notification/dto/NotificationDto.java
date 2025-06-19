package skillbox.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skillbox.notification.model.MicroServiceName;
import skillbox.notification.model.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private OffsetDateTime sentTime;
    private UUID authorId;
    private UUID receiverId;
    private UUID eventId;
    private String content;
    private MicroServiceName serviceName;
    private NotificationType notificationType;
    private Boolean isReaded;
}
