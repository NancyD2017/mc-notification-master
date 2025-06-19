package skillbox.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skillbox.notification.model.MicroServiceName;
import skillbox.notification.model.NotificationType;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationInputDto {
    private UUID authorId;
    private UUID userId;
    private UUID eventId;
    private String content;
    private MicroServiceName serviceName;
    private NotificationType notificationType;
}
