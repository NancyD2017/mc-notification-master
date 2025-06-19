package skillbox.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import skillbox.notification.dto.NotificationDto;
import skillbox.notification.dto.NotificationInputDto;
import skillbox.notification.service.NotificationService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final NotificationService notificationService;

    @MessageMapping("/send-public")
    @SendTo("/topic/public-notifications")
    public NotificationDto handlePublicNotification(
            @Payload NotificationInputDto notificationInputDto,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("Received public notification from session {}: {}",
                headerAccessor.getSessionId(), notificationInputDto);

        return notificationService.addNotification(notificationInputDto);
    }

    @MessageMapping("/send-private")
    @SendToUser("/queue/private-notifications")
    public NotificationDto handlePrivateNotification(
            @Payload NotificationInputDto notificationInputDto,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("Received private notification from session {}: {}",
                headerAccessor.getSessionId(), notificationInputDto);

        return notificationService.addNotification(notificationInputDto);
    }
}