package skillbox.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skillbox.notification.dto.NotificationDto;
import skillbox.notification.dto.NotificationInputDto;
import skillbox.notification.model.MicroServiceName;
import skillbox.notification.model.Notification;
import skillbox.notification.model.NotificationType;
import skillbox.notification.repository.NotificationRepository;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private NotificationInputDto inputDto;
    private Notification notification;
    private OffsetDateTime sentTime;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        sentTime = OffsetDateTime.now();

        inputDto = new NotificationInputDto();
        inputDto.setUserId(userId);
        inputDto.setAuthorId(UUID.randomUUID());
        inputDto.setContent("Test Notification");
        inputDto.setNotificationType(NotificationType.POST_COMMENT);
        inputDto.setServiceName(MicroServiceName.MC_NOTIFICATION);

        notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setReceiverId(userId);
        notification.setAuthorId(inputDto.getAuthorId());
        notification.setContent("Test Notification");
        notification.setNotificationType(NotificationType.POST_COMMENT);
        notification.setReaded(false);
        notification.setSentTime(sentTime);
        notification.setServiceName(MicroServiceName.MC_NOTIFICATION);
    }

    @Test
    public void testAddNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDto result = notificationService.addNotification(inputDto);

        assertNotNull(result);
        assertEquals(notification.getId(), result.getId());
        assertEquals(inputDto.getAuthorId(), result.getAuthorId());
        assertEquals("Test Notification", result.getContent());
        assertEquals(NotificationType.POST_COMMENT, result.getNotificationType());
        assertEquals(MicroServiceName.MC_NOTIFICATION, result.getServiceName());
        assertFalse(result.getIsReaded());
        assertNotNull(result.getSentTime());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testAddNotification_NullFields_ReturnsNull() {
        NotificationInputDto invalidInput = new NotificationInputDto();
        invalidInput.setAuthorId(UUID.randomUUID());
        invalidInput.setContent("Test");
        invalidInput.setServiceName(MicroServiceName.MC_NOTIFICATION);

        NotificationDto result = notificationService.addNotification(invalidInput);
        assertNull(result);

        invalidInput.setNotificationType(NotificationType.POST_COMMENT);
        invalidInput.setContent(null);
        result = notificationService.addNotification(invalidInput);
        assertNull(result);

        invalidInput.setContent("Test");
        invalidInput.setAuthorId(null);
        result = notificationService.addNotification(invalidInput);
        assertNull(result);

        invalidInput.setContent("Test");
        invalidInput.setAuthorId(UUID.randomUUID());
        invalidInput.setUserId(null);
        result = notificationService.addNotification(invalidInput);
        assertNull(result);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testGetNotifications_ReturnsList() {
        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());
        notification2.setReceiverId(userId);
        notification2.setAuthorId(UUID.randomUUID());
        notification2.setContent("Another Notification");
        notification2.setNotificationType(NotificationType.FRIEND_REQUEST);
        notification2.setReaded(false);
        notification2.setSentTime(sentTime);
        notification2.setServiceName(MicroServiceName.MC_NOTIFICATION);

        List<Notification> notifications = Arrays.asList(notification, notification2);
        when(notificationRepository.findByReceiverId(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.getNotifications(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        NotificationDto dto1 = result.get(0);
        assertEquals(notification.getId(), dto1.getId());
        assertEquals("Test Notification", dto1.getContent());
        assertFalse(dto1.getIsReaded());

        NotificationDto dto2 = result.get(1);
        assertEquals(notification2.getId(), dto2.getId());
        assertEquals("Another Notification", dto2.getContent());
        assertFalse(dto2.getIsReaded());

        verify(notificationRepository, times(1)).findByReceiverId(userId);
    }

    @Test
    public void testGetNotifications_EmptyList() {
        when(notificationRepository.findByReceiverId(userId)).thenReturn(Collections.emptyList());

        List<NotificationDto> result = notificationService.getNotifications(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationRepository, times(1)).findByReceiverId(userId);
    }

    @Test
    public void testGetUnreadCount_ReturnsCount() {
        when(notificationRepository.countByReceiverIdAndIsReadedFalse(userId)).thenReturn(5L);

        Long result = notificationService.getUnreadCount(userId);

        assertEquals(5L, result);
        verify(notificationRepository, times(1)).countByReceiverIdAndIsReadedFalse(userId);
    }

    @Test
    public void testGetUnreadCount_ZeroCount() {
        when(notificationRepository.countByReceiverIdAndIsReadedFalse(userId)).thenReturn(0L);

        Long result = notificationService.getUnreadCount(userId);

        assertEquals(0L, result);
        verify(notificationRepository, times(1)).countByReceiverIdAndIsReadedFalse(userId);
    }

    @Test
    public void testMarkAsRead_Success() {
        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());
        notification2.setReceiverId(userId);
        notification2.setAuthorId(UUID.randomUUID());
        notification2.setContent("Another Notification");
        notification2.setNotificationType(NotificationType.FRIEND_REQUEST);
        notification2.setReaded(false);
        notification2.setSentTime(sentTime);
        notification2.setServiceName(MicroServiceName.MC_NOTIFICATION);

        List<Notification> notifications = Arrays.asList(notification, notification2);
        when(notificationRepository.findByReceiverId(userId)).thenReturn(notifications);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.markAsRead(userId);

        verify(notificationRepository, times(1)).findByReceiverId(userId);
        verify(notificationRepository, times(2)).save(any(Notification.class));

        assertTrue(notifications.get(0).isReaded());
        assertTrue(notifications.get(1).isReaded());
    }

    @Test
    public void testMarkAsRead_EmptyNotifications() {
        when(notificationRepository.findByReceiverId(userId)).thenReturn(Collections.emptyList());

        notificationService.markAsRead(userId);

        verify(notificationRepository, times(1)).findByReceiverId(userId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testGetNotifications_NullUserId() {
        List<NotificationDto> result = notificationService.getNotifications(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationRepository, times(1)).findByReceiverId(any());
    }

    @Test
    public void testGetUnreadCount_NullUserId() {
        Long result = notificationService.getUnreadCount(null);

        assertEquals(result, 0);
        verify(notificationRepository, times(1)).countByReceiverIdAndIsReadedFalse(any());
    }
}