package skillbox.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skillbox.notification.dto.NotificationDto;
import skillbox.notification.dto.NotificationInputDto;
import skillbox.notification.model.Notification;
import skillbox.notification.repository.NotificationRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationDto addNotification(NotificationInputDto input){
        if (input.getNotificationType() == null || input.getUserId() == null ||
                input.getAuthorId() == null || input.getContent() == null) return null;
        Notification notification = new Notification();
        notification.setReceiverId(input.getUserId());
        notification.setAuthorId(input.getAuthorId());
        notification.setContent(input.getContent());
        notification.setNotificationType(input.getNotificationType());
        notification.setReaded(false);
        notification.setSentTime(OffsetDateTime.now());
        notification.setServiceName(input.getServiceName());

        notification = notificationRepository.save(notification);
        return convertToDto(notification);
    }
    public List<NotificationDto> getNotifications(UUID userId){
        return notificationRepository.findByReceiverId(userId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void markAsRead(UUID userId){
        notificationRepository.findByReceiverId(userId).forEach(n -> {
            n.setReaded(true);
            notificationRepository.save(n);
        });
    }
    public Long getUnreadCount(UUID userId){
        return notificationRepository.countByReceiverIdAndIsReadedFalse(userId);
    }

    private NotificationDto convertToDto(Notification notification){
        return new NotificationDto(
                notification.getId(),
                notification.getSentTime(),
                notification.getAuthorId(),
                notification.getReceiverId(),
                notification.getEventId(),
                notification.getContent(),
                notification.getServiceName(),
                notification.getNotificationType(),
                notification.isReaded()
        );
    }
}
