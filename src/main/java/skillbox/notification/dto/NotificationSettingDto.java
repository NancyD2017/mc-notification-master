package skillbox.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingDto {
    private UUID id;

    private Boolean enablePost;
    private Boolean enablePostComment;
    private Boolean enableCommentComment;
    private Boolean enableFriendRequest;
    private Boolean enableFriendBirthday;
    private Boolean enableMessage;
    private Boolean enableSendEmailMessage;
}
