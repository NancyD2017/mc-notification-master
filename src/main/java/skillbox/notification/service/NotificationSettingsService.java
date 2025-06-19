package skillbox.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import skillbox.notification.dto.NotificationSettingDto;
import skillbox.notification.dto.NotificationSettingsDto;
import skillbox.notification.dto.SettingRq;
import skillbox.notification.dto.SettingsDto;
import skillbox.notification.model.NotificationSettings;
import skillbox.notification.model.NotificationType;
import skillbox.notification.repository.NotificationSettingsRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingsService {
    private final NotificationSettingsRepository repository;

    public NotificationSettingDto getSettings(UUID userId){
        Optional<NotificationSettings> settings = repository.findByUserId(userId);
        if (!settings.isPresent()) {
            NotificationSettings sd = createDefaultSettings(userId);
            return convertToDtoS(sd);
        }

        return settings.map(this::convertToDtoS).orElse(null);
    }

    public SettingsDto createSettings(UUID userId, SettingsDto request){
        if (repository.findByUserId(userId).isPresent()) return null;

        NotificationSettings settings = new NotificationSettings();
        settings.setUserId(userId);
        settings.setFriendRequest(request.isFriendRequest());
        settings.setFriendBirthday(request.isFriendBirthday());
        settings.setPostComment(request.isPostComment());
        settings.setCommentComment(request.isCommentComment());
        settings.setPost(request.isPost());
        settings.setMessage(request.isMessage());
        settings.setLikeMessage(request.isLikeMessage());
        settings.setSendEmailMessage(request.isSendEmailMessage());
        settings = repository.save(settings);
        request.setId(settings.getId());

        return request;
    }

    public NotificationSettingsDto updateSettings(UUID userId, SettingRq request){
        Optional<NotificationSettings> settings = repository.findByUserId(userId);
        if (settings.isEmpty()) return null;

        if (updateSettings(settings.get(), request.getEnable(), request.getNotificationType()) == 1) return null;

        NotificationSettings ns = repository.save(settings.get());
        return convertToDto(ns);
    }

    private int updateSettings(NotificationSettings settings, Boolean enable, NotificationType notificationType) {
            switch (notificationType) {
                case FRIEND_REQUEST -> settings.setFriendRequest(enable);
                case FRIEND_BIRTHDAY -> settings.setFriendBirthday(enable);
                case POST_COMMENT -> settings.setPostComment(enable);
                case COMMENT_COMMENT -> settings.setCommentComment(enable);
                case POST -> settings.setPost(enable);
                case MESSAGE -> settings.setMessage(enable);
                case SEND_EMAIL_MESSAGE -> settings.setSendEmailMessage(enable);
                case LIKE_MESSAGE -> settings.setLikeMessage(enable);
                default -> {return 1;}
            }
        return 0;
    }
    public NotificationSettings createDefaultSettings(UUID userId){
        NotificationSettings settings = new NotificationSettings();
        settings.setUserId(userId);
        settings.setFriendRequest(false);
        settings.setFriendBirthday(false);
        settings.setCommentComment(false);
        settings.setPostComment(false);
        settings.setPost(false);
        settings.setMessage(false);
        settings.setLikeMessage(false);
        settings.setSendEmailMessage(false);
        settings = repository.save(settings);
        return settings;
    }


    private NotificationSettingsDto convertToDto(NotificationSettings settings) {
        return new NotificationSettingsDto(
                OffsetDateTime.now(),
                List.of(
                        new SettingRq(settings.getPostComment(), NotificationType.POST_COMMENT),
                        new SettingRq(settings.getFriendBirthday(), NotificationType.FRIEND_BIRTHDAY),
                        new SettingRq(settings.getFriendRequest(), NotificationType.FRIEND_REQUEST),
                        new SettingRq(settings.getCommentComment(),NotificationType.COMMENT_COMMENT),
                        new SettingRq(settings.getMessage(),  NotificationType.MESSAGE),
                        new SettingRq(settings.getPost(), NotificationType.POST),
                        new SettingRq(settings.getSendEmailMessage(),  NotificationType.SEND_EMAIL_MESSAGE),
                        new SettingRq(settings.getLikeMessage(), NotificationType.LIKE_MESSAGE)
                ),
                settings.getUserId()
        );
    }

    private NotificationSettingDto convertToDtoS(NotificationSettings settings) {
        return new NotificationSettingDto(
                        settings.getId(),
                        settings.getPost(),
                        settings.getPostComment(),
                settings.getCommentComment(),
                settings.getFriendRequest(),
                settings.getFriendBirthday(),
                settings.getMessage(),
                settings.getSendEmailMessage()
        );
    }
}
