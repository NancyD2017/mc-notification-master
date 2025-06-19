package skillbox.notification.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultRs {
    @Schema(description = "Время операции", example = "2023-01-01T00:00:00Z")
    private OffsetDateTime time;

    @Schema(description = "Статус операции")
    private MassageRs status;
}
