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
public class CountRs {
    @Schema(description = "Количество непрочитанных уведомлений", example = "5")
    private Long count;
}
