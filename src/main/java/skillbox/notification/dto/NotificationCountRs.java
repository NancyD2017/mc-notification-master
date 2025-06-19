package skillbox.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCountRs {
    @Schema(description = "Unix timestamp ответа", example = "1672531200000")
    private Long timestamp;

    @Schema(description = "Данные с количеством")
    private CountRs data;
}
