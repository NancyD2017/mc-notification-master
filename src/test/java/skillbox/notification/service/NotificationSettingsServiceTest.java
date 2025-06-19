package skillbox.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skillbox.notification.dto.NotificationSettingDto;
import skillbox.notification.dto.NotificationSettingsDto;
import skillbox.notification.dto.SettingRq;
import skillbox.notification.dto.SettingsDto;
import skillbox.notification.model.NotificationSettings;
import skillbox.notification.model.NotificationType;
import skillbox.notification.repository.NotificationSettingsRepository;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationSettingsServiceTest {

    @Mock
    private NotificationSettingsRepository repository;

    @InjectMocks
    private NotificationSettingsService notificationSettingsService;

    private UUID userId;
    private NotificationSettings settings;
    private SettingsDto settingsDto;
    private SettingRq settingRq;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();

        settings = new NotificationSettings();
        settings.setId(UUID.randomUUID());
        settings.setUserId(userId);
        settings.setFriendRequest(true);
        settings.setFriendBirthday(true);
        settings.setPostComment(true);
        settings.setCommentComment(true);
        settings.setPost(true);
        settings.setMessage(true);
        settings.setLikeMessage(true);
        settings.setSendEmailMessage(true);

        settingsDto = new SettingsDto();
        settingRq = new SettingRq(true, NotificationType.POST_COMMENT);
    }

    @Test
    public void testGetSettings_Success() {
        when(repository.findByUserId(userId)).thenReturn(Optional.of(settings));

        NotificationSettingDto result = notificationSettingsService.getSettings(userId);

        assertNotNull(result);
        assertEquals(true, result.getEnablePostComment());
        verify(repository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetSettings_NotFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        NotificationSettings defaultSettings = new NotificationSettings();
        defaultSettings.setUserId(userId);
        defaultSettings.setFriendRequest(true);
        defaultSettings.setFriendBirthday(true);
        defaultSettings.setPostComment(true);
        defaultSettings.setCommentComment(true);
        defaultSettings.setPost(true);
        defaultSettings.setMessage(true);
        defaultSettings.setLikeMessage(true);
        defaultSettings.setSendEmailMessage(true);
        defaultSettings.setId(UUID.randomUUID());

        when(repository.save(any(NotificationSettings.class))).thenReturn(defaultSettings);

        NotificationSettingDto result = notificationSettingsService.getSettings(userId);

        assertNotNull(result);
        assertEquals(result.getEnablePostComment(), true);
        assertEquals(result.getEnablePost(), true);
        assertEquals(result.getEnableMessage(), true);
        assertEquals(result.getEnableCommentComment(), true);
        assertEquals(result.getEnableFriendBirthday(), true);
        assertEquals(result.getEnableFriendRequest(), true);
        assertEquals(result.getEnableSendEmailMessage(), true);
        verify(repository, times(1)).findByUserId(userId);
        verify(repository, times(1)).save(any(NotificationSettings.class));
    }


    @Test
    public void testCreateSettings_Success() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any(NotificationSettings.class))).thenReturn(settings);

        SettingsDto result = notificationSettingsService.createSettings(userId, settingsDto);

        assertNotNull(result);
        assertEquals(settings.getId(), result.getId());
        verify(repository, times(1)).findByUserId(userId);
        verify(repository, times(1)).save(any(NotificationSettings.class));
    }

    @Test
    public void testCreateSettings_AlreadyExists() {
        when(repository.findByUserId(userId)).thenReturn(Optional.of(settings));

        SettingsDto result = notificationSettingsService.createSettings(userId, settingsDto);

        assertNull(result);
        verify(repository, times(1)).findByUserId(userId);
        verify(repository, never()).save(any(NotificationSettings.class));
    }

    @Test
    public void testUpdateSettings_Success() {
        when(repository.findByUserId(userId)).thenReturn(Optional.of(settings));
        when(repository.save(any(NotificationSettings.class))).thenReturn(settings);

        settingRq = new SettingRq(false, NotificationType.POST_COMMENT);
        NotificationSettingsDto result = notificationSettingsService.updateSettings(userId, settingRq);

        assertNotNull(result);
        assertEquals(userId, result.getUser_id());
        assertTrue(result.getData().stream().anyMatch(s -> s.getNotificationType() == NotificationType.POST_COMMENT && !s.getEnable()));
        verify(repository, times(1)).findByUserId(userId);
        verify(repository, times(1)).save(any(NotificationSettings.class));
    }

    @Test
    public void testUpdateSettings_NotFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        NotificationSettingsDto result = notificationSettingsService.updateSettings(userId, settingRq);

        assertNull(result);
        verify(repository, times(1)).findByUserId(userId);
        verify(repository, never()).save(any(NotificationSettings.class));
    }

    @Test
    public void testCreateDefaultSettings() {
        when(repository.save(any(NotificationSettings.class))).thenReturn(settings);

        NotificationSettings result = notificationSettingsService.createDefaultSettings(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.getFriendRequest());
        assertTrue(result.getFriendBirthday());
        assertTrue(result.getPostComment());
        assertTrue(result.getCommentComment());
        assertTrue(result.getPost());
        assertTrue(result.getMessage());
        assertTrue(result.getLikeMessage());
        assertTrue(result.getSendEmailMessage());
        verify(repository, times(1)).save(any(NotificationSettings.class));
    }
}