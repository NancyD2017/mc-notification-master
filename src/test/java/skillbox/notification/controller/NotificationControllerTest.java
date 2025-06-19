package skillbox.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import skillbox.notification.JwtTestUtils;
import skillbox.notification.dto.*;
import skillbox.notification.model.MicroServiceName;
import skillbox.notification.model.NotificationType;
import skillbox.notification.service.AccountService;
import skillbox.notification.service.NotificationService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void send_shouldReturnSentNotification() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        NotificationInputDto request = new NotificationInputDto();
        request.setContent("Hello");
        request.setNotificationType(NotificationType.POST_COMMENT);
        request.setUserId(userId);
        request.setAuthorId(authorId);
        request.setServiceName(MicroServiceName.MC_NOTIFICATION);

        NotificationDto response = new NotificationDto();
        response.setId(UUID.randomUUID());
        response.setAuthorId(authorId);
        response.setReceiverId(userId);
        response.setContent("Hello");
        response.setNotificationType(NotificationType.POST_COMMENT);
        response.setSentTime(OffsetDateTime.now());
        response.setIsReaded(false);

        when(notificationService.addNotification(any(NotificationInputDto.class)))
                .thenReturn(response);

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(post("/api/v1/notifications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(authorId.toString()))
                .andExpect(jsonPath("$.content").value("Hello"));
    }

    @Test
    void getUserNotifications_shouldReturnNotificationList() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        NotificationDto dto1 = new NotificationDto();
        dto1.setId(UUID.randomUUID());
        dto1.setAuthorId(authorId);
        dto1.setReceiverId(userId);
        dto1.setContent("Message 1");
        dto1.setNotificationType(NotificationType.FRIEND_REQUEST);
        dto1.setSentTime(OffsetDateTime.now());
        dto1.setIsReaded(false);

        NotificationDto dto2 = new NotificationDto();
        dto2.setId(UUID.randomUUID());
        dto2.setAuthorId(authorId);
        dto2.setReceiverId(userId);
        dto2.setContent("Message 2");
        dto2.setNotificationType(NotificationType.POST_COMMENT);
        dto2.setSentTime(OffsetDateTime.now());
        dto2.setIsReaded(false);

        AccountDataDto account = new AccountDataDto("Anastasia", "Alifanowa", "https://example.com/photo.jpg");
        when(accountService.getAccount(authorId)).thenReturn(account);
        when(notificationService.getNotifications(userId)).thenReturn(List.of(dto1, dto2));

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].data.content").value("Message 1"))
                .andExpect(jsonPath("$.content[0].data.author.firstName").value("Anastasia"))
                .andExpect(jsonPath("$.content[1].data.content").value("Message 2"))
                .andExpect(jsonPath("$.content[1].data.author.firstName").value("Anastasia"));
    }

    @Test
    void send_shouldReturnBadRequest_onMissingContent() throws Exception {
        UUID userId = UUID.randomUUID();
        NotificationInputDto request = new NotificationInputDto();
        request.setUserId(userId);
        request.setAuthorId(UUID.randomUUID());
        request.setNotificationType(NotificationType.POST_COMMENT);

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        when(notificationService.addNotification(any(NotificationInputDto.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/notifications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserNotifications_shouldReturnEmptyList() throws Exception {
        UUID userId = UUID.randomUUID();

        when(notificationService.getNotifications(userId)).thenReturn(List.of());

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void markAsRead_shouldReturnSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtils.generateTokenWithUserId(userId);

        doNothing().when(notificationService).markAsRead(userId);

        mockMvc.perform(put("/api/v1/notifications/readed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.massage").value(true));

        verify(notificationService, times(1)).markAsRead(userId);
    }

    @Test
    void getCount_shouldReturnUnreadCount() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtils.generateTokenWithUserId(userId);

        when(notificationService.getUnreadCount(userId)).thenReturn(3L);

        mockMvc.perform(get("/api/v1/notifications/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(3));

        verify(notificationService, times(1)).getUnreadCount(userId);
    }

    @Test
    void getCount_shouldReturnBadRequest_onNullCount() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtils.generateTokenWithUserId(userId);

        when(notificationService.getUnreadCount(userId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/notifications/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        verify(notificationService, times(1)).getUnreadCount(userId);
    }
}