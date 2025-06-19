package skillbox.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import skillbox.notification.dto.events.NotificationEvent;
import skillbox.notification.model.MailBody;
import skillbox.notification.model.Notification;
import skillbox.notification.model.NotificationSettings;
import skillbox.notification.model.NotificationType;
import skillbox.notification.repository.NotificationRepository;
import skillbox.notification.repository.NotificationSettingsRepository;
import skillbox.notification.service.EmailService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;

    @KafkaListener(topics = "account.events", groupId = "notification-group")
    public void handleAccountEvent(NotificationEvent event) {
        log.info("Received account event: {}", event);

        switch (event.getNotificationType()) {
            case FRIEND_BIRTHDAY -> handleFriendBirthday(event);
            case POST_COMMENT -> handlePostComment(event);
            case FRIEND_REQUEST -> handleFriendRequest(event);
            case COMMENT_COMMENT -> handleCommentComment(event);
            case POST -> handlePost(event);
            case MESSAGE -> handleMessage(event);
            default -> log.warn("Unknown event type: {}", event.getNotificationType());
        }
    }

    private void handleFriendBirthday(NotificationEvent event) {
        NotificationSettings ns = notificationSettingsRepository.findByUserId(event.getReceiverId()).orElse(null);
        List<Notification> notifications = notificationRepository.findByContentAndReceiverId(event.getContent(), event.getReceiverId());
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Moscow"));
        log.info("notifications: {}, today: {}, content: {}", notifications, today, event.getContent());

        if (ns != null && ns.getFriendBirthday()) {
            for (Notification notification : notifications) {
                LocalDate notificationDate = notification.getSentTime().toLocalDate();
                log.info("notificationDate: {}", notificationDate);
                if (notificationDate.isEqual(today)) {
                    return;
                }
            }
            createAndSendNotification(
                    event,
                    NotificationType.FRIEND_BIRTHDAY,
                    event.getContent(),
                    ns.getSendEmailMessage()
            );
        }
    }

    private void handlePostComment(NotificationEvent event) {
        NotificationSettings ns = notificationSettingsRepository.findByUserId(event.getReceiverId()).orElse(null);
        if (ns != null && ns.getPostComment()) createAndSendNotification(
                event,
                NotificationType.POST_COMMENT,
                event.getContent(),
                ns.getSendEmailMessage()
        );
    }

    private void handleFriendRequest(NotificationEvent event) {
        NotificationSettings ns = notificationSettingsRepository.findByUserId(event.getReceiverId()).orElse(null);
        if (ns != null && ns.getFriendRequest()) createAndSendNotification(
                event,
                NotificationType.FRIEND_REQUEST,
                event.getContent(),
                ns.getSendEmailMessage()
        );
    }

    private void handleCommentComment(NotificationEvent event) {
        NotificationSettings ns = notificationSettingsRepository.findByUserId(event.getReceiverId()).orElse(null);
        if (ns != null && ns.getCommentComment()) createAndSendNotification(
                event,
                NotificationType.COMMENT_COMMENT,
                event.getContent(),
                ns.getSendEmailMessage()
        );
    }

    private void handlePost(NotificationEvent event) {
        NotificationSettings ns = notificationSettingsRepository.findByUserId(event.getReceiverId()).orElse(null);
        if (ns != null && ns.getPost()) {
            if (event.getReceiverIds() != null) {
                for (UUID u: event.getReceiverIds()){
                    createAndSendNotifications(
                            event,
                            NotificationType.POST,
                            event.getContent(),
                            ns.getSendEmailMessage(),
                            u
                    );
                }
            }
                else createAndSendNotification(
                    event,
                    NotificationType.POST,
                    event.getContent(),
                    ns.getSendEmailMessage()
            );
        }
    }

    private void handleMessage(NotificationEvent event) {
        NotificationSettings ns = notificationSettingsRepository.findByUserId(event.getReceiverId()).orElse(null);
        if (ns != null && ns.getMessage()) createAndSendNotification(
                event,
                NotificationType.MESSAGE,
                event.getContent(),
                ns.getSendEmailMessage()
        );
    }

    private void createAndSendNotifications(NotificationEvent event, NotificationType type, String content, boolean isEmail, UUID uuid) {
        Notification notification = new Notification();
        notification.setReceiverId(uuid);
        notification.setNotificationType(type);
        notification.setContent(content);
        notification.setSentTime(OffsetDateTime.now());
        notification.setReaded(false);
        notification.setServiceName(event.getServiceName());
        notification.setEventId(event.getEventId() != null ? event.getEventId() : UUID.randomUUID());
        notification.setAuthorId(event.getAuthorId() != null ? event.getAuthorId() : UUID.randomUUID());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification: {}", savedNotification);

        if (isEmail) sendEmailNotification(event.getReceiverId(), savedNotification, event.getEmail());
        sendWebSocketNotification(event.getReceiverId(), savedNotification);
    }

    private void createAndSendNotification(NotificationEvent event, NotificationType type, String content, boolean isEmail) {
        Notification notification = new Notification();
        notification.setReceiverId(event.getReceiverId());
        notification.setNotificationType(type);
        notification.setContent(content);
        notification.setSentTime(OffsetDateTime.now());
        notification.setReaded(false);
        notification.setServiceName(event.getServiceName());
        notification.setEventId(event.getEventId() != null ? event.getEventId() : UUID.randomUUID());
        notification.setAuthorId(event.getAuthorId() != null ? event.getAuthorId() : UUID.randomUUID());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification: {}", savedNotification);

        if (isEmail) sendEmailNotification(event.getReceiverId(), savedNotification, event.getEmail());
        sendWebSocketNotification(event.getReceiverId(), savedNotification);
        }

    private void sendWebSocketNotification(UUID userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                convertToEvent(notification)
        );
    }

    private void sendEmailNotification(UUID userId, Notification notification, String email) {
        try {
            MailBody mailBody = new MailBody(
                    email,
                    "Новое уведомление в CODE LOUNGE",
                    notification.getContent()
            );
            emailService.sendSimpleMessage(mailBody);
            log.info("Sent email notification to {} for user {}", email, userId);
        } catch (Exception e){
            log.error("Invalid email");
        }
    }

    private NotificationEvent convertToEvent(Notification notification) {
        NotificationEvent event = new NotificationEvent();
        event.setReceiverId(notification.getReceiverId());
        event.setNotificationType(notification.getNotificationType());
        event.setContent(notification.getContent());
        event.setSentTime(notification.getSentTime());
        event.setServiceName(notification.getServiceName());
        event.setEventId(notification.getEventId());
        event.setAuthorId(notification.getAuthorId());
        event.setIsRead(false);
        log.info("convertedToEvent");
        return event;
    }
}