package skillbox.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSentDto {
    @Schema(description = "Временная метка ответа", example = "2025-06-06T13:46:40.581Z")
    private OffsetDateTime timeStamp;

    @Schema(description = "Список уведомлений")
    private List<NotificationWrapperDto> content;
}