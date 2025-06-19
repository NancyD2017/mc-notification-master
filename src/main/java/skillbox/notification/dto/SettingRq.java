package skillbox.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skillbox.notification.model.NotificationType;

@Getter
@Setter
@NoArgsConstructor
public class SettingRq {
    private Boolean enable;
    private NotificationType notificationType;

    public SettingRq(Boolean enable, NotificationType notificationType) {
        this.enable = enable;
        this.notificationType = notificationType;
    }
}
