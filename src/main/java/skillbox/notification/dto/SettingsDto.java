package skillbox.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {
    private UUID id;
    private UUID userId;
    private boolean friendRequest;
    private boolean friendBirthday;
    private boolean postComment;
    private boolean commentComment;
    private boolean post;
    private boolean message;
    private boolean likeMessage;
    private boolean sendPhoneMessage;
    private boolean sendEmailMessage;
}
