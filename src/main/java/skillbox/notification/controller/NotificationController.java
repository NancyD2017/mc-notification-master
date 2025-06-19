package skillbox.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skillbox.notification.dto.*;
import skillbox.notification.model.DefaultRs;
import skillbox.notification.model.MassageRs;
import skillbox.notification.service.AccountService;
import skillbox.notification.service.NotificationService;
import skillbox.notification.utils.JwtTokenUtils;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<NotificationSentDto> getNotifications(
            @RequestHeader(name = "Authorization") String token) {
        log.info("getNotifications with token: {}", token);
        try {
            String userId = JwtTokenUtils.parseJwtToken(token).get("userId").toString();
            UUID uuid = UUID.fromString(userId);
            List<NotificationDto> notifications = notificationService.getNotifications(uuid);
            NotificationSentDto response = new NotificationSentDto();
            response.setTimeStamp(OffsetDateTime.now());
            response.setContent(notifications.stream()
                    .map(this::convertToWrapperDto)
                    .collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get notifications: {}", e.getMessage(), e);
            NotificationSentDto response = new NotificationSentDto();
            response.setTimeStamp(OffsetDateTime.now());
            response.setContent(Collections.emptyList());
            return ResponseEntity.ok(response);
        }
    }

    private NotificationWrapperDto convertToWrapperDto(NotificationDto notificationDto) {
        NotificationDataRs dataRs = new NotificationDataRs(
                notificationDto.getId(),
                accountService.getAccount(notificationDto.getAuthorId()),
                notificationDto.getReceiverId(),
                notificationDto.getContent(),
                notificationDto.getNotificationType(),
                notificationDto.getSentTime(),
                notificationDto.getEventId(),
                notificationDto.getIsReaded(),
                notificationDto.getAuthorId()
        );
        return new NotificationWrapperDto(dataRs);
    }

    private NotificationInputDto convertToInput(NotificationDto notificationDto) {
        return new NotificationInputDto(
                notificationDto.getAuthorId(),
                notificationDto.getReceiverId(),
                notificationDto.getEventId(),
                notificationDto.getContent(),
                notificationDto.getServiceName(),
                notificationDto.getNotificationType()
        );
    }

    @PostMapping
    public ResponseEntity<NotificationDto> addNotification(
            @RequestBody NotificationInputDto input) {
        NotificationDto nd = notificationService.addNotification(input);
        log.info("Notification added: {}", nd);
        return nd == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(nd);
    }

    @PutMapping("/readed")
    public ResponseEntity<DefaultRs> markAsRead(
            @RequestHeader(name = "Authorization") String token) {
        String userId = JwtTokenUtils.parseJwtToken(token).get("userId").toString();
        notificationService.markAsRead(UUID.fromString(userId));
        return ResponseEntity.ok(new DefaultRs(OffsetDateTime.now(), new MassageRs(true)));
    }

    @GetMapping("/count")
    public ResponseEntity<NotificationCountRs> getCount(
            @RequestHeader(name = "Authorization") String token) {
        log.info("getCount with token: {}", token);
        try {
            String userId = JwtTokenUtils.parseJwtToken(token).get("userId").toString();
            UUID uuid = UUID.fromString(userId);
            Long l = notificationService.getUnreadCount(uuid);
            if (l == null) {
                return ResponseEntity.badRequest().build();
            }
            NotificationCountRs response = new NotificationCountRs(
                    System.currentTimeMillis(),
                    new CountRs(l)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get notification count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}