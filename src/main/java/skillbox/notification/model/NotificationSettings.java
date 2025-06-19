package skillbox.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "notification_settings", schema = "notifications_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "friend_request")
    private Boolean friendRequest;

    @Column(name = "friend_birthday")
    private Boolean friendBirthday;

    @Column(name = "post_comment")
    private Boolean postComment;

    @Column(name = "comment_comment")
    private Boolean commentComment;

    private Boolean post;

    private Boolean message;

    @Column(name = "like_message")
    private Boolean likeMessage;

    @Column(name = "send_email_message")
    private Boolean sendEmailMessage;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
