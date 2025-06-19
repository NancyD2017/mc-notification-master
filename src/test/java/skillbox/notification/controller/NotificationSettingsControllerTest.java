package skillbox.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import skillbox.notification.JwtTestUtils;
import skillbox.notification.dto.NotificationSettingDto;
import skillbox.notification.dto.SettingsDto;
import skillbox.notification.service.NotificationSettingsService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationSettingsController.class)
public class NotificationSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationSettingsService notificationSettingsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getSettings_shouldReturnSettings() throws Exception {
        UUID userId = UUID.randomUUID();
        NotificationSettingDto mockDto = new NotificationSettingDto();
        mockDto.setId(UUID.randomUUID());
        mockDto.setEnablePost(true);

        when(notificationSettingsService.getSettings(userId)).thenReturn(mockDto);

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(get("/api/v1/notifications/settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enablePost").value(true))
                .andExpect(jsonPath("$.enablePostComment").doesNotExist())
                .andExpect(jsonPath("$.enableCommentComment").doesNotExist())
                .andExpect(jsonPath("$.enableFriendRequest").doesNotExist())
                .andExpect(jsonPath("$.enableFriendBirthday").doesNotExist())
                .andExpect(jsonPath("$.enableMessage").doesNotExist())
                .andExpect(jsonPath("$.enableSendEmailMessage").doesNotExist());
    }

    @Test
    void createSettings_shouldCreateAndReturnSettings() throws Exception {
        UUID userId = UUID.randomUUID();
        SettingsDto request = new SettingsDto();
        request.setId(UUID.randomUUID());

        when(notificationSettingsService.createSettings(eq(userId), any(SettingsDto.class)))
                .thenReturn(request);

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(post("/api/v1/notifications/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId().toString()));
    }

    @Test
    void getSettings_shouldReturnNotFound_whenUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(notificationSettingsService.getSettings(userId)).thenReturn(null);

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(get("/api/v1/notifications/settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSettings_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        UUID userId = UUID.randomUUID();

        String token = JwtTestUtils.generateTokenWithUserId(userId);

        mockMvc.perform(post("/api/v1/notifications/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}