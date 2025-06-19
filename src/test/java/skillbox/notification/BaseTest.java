package skillbox.notification;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import skillbox.notification.dto.NotificationDto;
import skillbox.notification.dto.NotificationInputDto;
import skillbox.notification.dto.NotificationSettingsDto;
import skillbox.notification.dto.SettingRq;
import skillbox.notification.dto.SettingsDto;
import skillbox.notification.model.Notification;
import skillbox.notification.model.NotificationSettings;
import skillbox.notification.model.NotificationType;
import skillbox.notification.repository.NotificationRepository;
import skillbox.notification.repository.NotificationSettingsRepository;
import skillbox.notification.service.NotificationService;
import skillbox.notification.service.NotificationSettingsService;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static skillbox.notification.model.MicroServiceName.MC_NOTIFICATION;

@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@Testcontainers
public abstract class BaseTest implements PostgreBaseTest {

    @Autowired
    protected NotificationSettingsService service;

    @Autowired
    protected NotificationSettingsRepository repository;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected NotificationSettingsRepository notificationSettingsRepository;

    @Mock
    protected ObjectMapper mapper;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        ObjectMapper realMapper = new ObjectMapper();
        Mockito.lenient().when(mapper.writeValueAsString(any(NotificationInputDto.class))).thenAnswer(invocation -> realMapper.writeValueAsString(invocation.getArgument(0)));
        Mockito.lenient().when(mapper.writeValueAsString(any(NotificationSettingsDto.class))).thenAnswer(invocation -> realMapper.writeValueAsString(invocation.getArgument(0)));
        Mockito.lenient().when(mapper.writeValueAsString(any(SettingRq.class))).thenAnswer(invocation -> realMapper.writeValueAsString(invocation.getArgument(0)));
        Mockito.lenient().when(mapper.writeValueAsString(any(SettingsDto.class))).thenAnswer(invocation -> realMapper.writeValueAsString(invocation.getArgument(0)));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS notification_schema");
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS notification_schema.notifications (
                id UUID PRIMARY KEY,
                receiver_id UUID NOT NULL,
                author_id UUID NOT NULL,
                content TEXT NOT NULL,
                event_id UUID,
                is_readed BOOLEAN DEFAULT FALSE,
                notification_type VARCHAR(50) NOT NULL,
                sent_time TIMESTAMP NOT NULL,
                service_name VARCHAR(100) NOT NULL
            )
            """);
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS notification_schema.notification_settings (
                id UUID PRIMARY KEY,
                receiver_id UUID NOT NULL,
                friend_request BOOLEAN DEFAULT FALSE,
                friend_birthday BOOLEAN DEFAULT FALSE,
                post_comment BOOLEAN DEFAULT FALSE,
                comment_comment BOOLEAN DEFAULT FALSE,
                post BOOLEAN DEFAULT FALSE,
                message BOOLEAN DEFAULT FALSE,
                like_message BOOLEAN DEFAULT FALSE,
                send_email_message BOOLEAN DEFAULT FALSE
            )
            """);
        NotificationDto mockNotification = new NotificationDto();
        mockNotification.setId(UUID.randomUUID());
        mockNotification.setAuthorId(UUID.randomUUID());
        mockNotification.setContent("Test Notification");
        mockNotification.setEventId(UUID.randomUUID());
        mockNotification.setNotificationType(NotificationType.POST_COMMENT);
        mockNotification.setReceiverId(UUID.randomUUID());
        mockNotification.setSentTime(OffsetDateTime.now());
        mockNotification.setServiceName(MC_NOTIFICATION);
        when(notificationService.getNotifications(any(UUID.class))).thenReturn(Collections.singletonList(mockNotification));
        when(notificationService.addNotification(any(NotificationInputDto.class))).thenReturn(mockNotification);
        when(notificationService.getUnreadCount(any(UUID.class))).thenReturn(1L);
    }

    protected ResultActions post(String path, Object pathVariable, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path, pathVariable)
                .content(mapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions post(String path, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path)
                .content(mapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions post(String path, String pathParamName, String pathParam, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path).param(pathParamName, pathParam)
                .content(mapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions post(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions post(String path, Object object, String headerName, String header) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path)
                .header(headerName, header)
                .content(mapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected void createTestNotification(UUID userId) {
        Notification n = new Notification();
        n.setSentTime(OffsetDateTime.now());
        n.setNotificationType(NotificationType.POST_COMMENT);
        n.setReceiverId(userId);
        n.setReaded(false);
        n.setEventId(UUID.randomUUID());
        n.setAuthorId(UUID.randomUUID());
        n.setContent("Some test notification");
        n.setServiceName(MC_NOTIFICATION);
        notificationRepository.save(n);
    }

    protected NotificationInputDto createTestNotificationInputDto(UUID userId) {
        NotificationInputDto n = new NotificationInputDto();
        n.setNotificationType(NotificationType.POST_COMMENT);
        n.setUserId(userId);
        n.setEventId(UUID.randomUUID());
        n.setAuthorId(UUID.randomUUID());
        n.setContent("Some test notification");
        n.setServiceName(MC_NOTIFICATION);
        return n;
    }

    protected void createTestNotificationSettings(UUID uuid) {
        NotificationSettings n = new NotificationSettings();
        n.setUserId(uuid);
        n.setLikeMessage(true);
        notificationSettingsRepository.save(n);
    }

    protected SettingsDto createTestSettingsDto(UUID uuid) {
        SettingsDto sd = new SettingsDto();
        sd.setCommentComment(true);
        sd.setUserId(uuid);
        return sd;
    }
}