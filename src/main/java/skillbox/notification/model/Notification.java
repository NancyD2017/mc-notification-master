package skillbox.notification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "notifications_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification{
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "author_id")
    private UUID authorId;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    @JsonProperty("notification_type")
    private NotificationType notificationType;

    @Column(name = "is_readed")
    @JsonProperty("is_readed")
    private boolean isReaded;

    @Column(name = "service_name")
    @JsonProperty("service_name")
    @Enumerated(EnumType.STRING)
    private MicroServiceName serviceName;

    @Column(name = "sent_time")
    @JsonProperty("sent_time")
    private OffsetDateTime sentTime;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
