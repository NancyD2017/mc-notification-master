package skillbox.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skillbox.notification.model.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDataRs {
    private UUID id;
    private AccountDataDto author;
    private UUID receiverId;
    private String content;
    private NotificationType notificationType;
    private OffsetDateTime sentTime;
    private UUID eventId;
    private Boolean isReaded;
    private UUID authorId;
}