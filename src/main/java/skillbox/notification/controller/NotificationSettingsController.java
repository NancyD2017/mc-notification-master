package skillbox.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skillbox.notification.dto.NotificationSettingDto;
import skillbox.notification.dto.SettingRq;
import skillbox.notification.dto.SettingsDto;
import skillbox.notification.model.DefaultRs;
import skillbox.notification.model.MassageRs;
import skillbox.notification.service.NotificationSettingsService;
import skillbox.notification.utils.JwtTokenUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/settings")
@RequiredArgsConstructor
public class NotificationSettingsController {
    private final NotificationSettingsService settingsService;

    @GetMapping
    public ResponseEntity<NotificationSettingDto> getSettings(
            @RequestHeader(name = "Authorization") String token) {
        UUID userId = UUID.fromString(JwtTokenUtils.parseJwtToken(token).get("userId").toString());
        NotificationSettingDto nsd = settingsService.getSettings(userId);
        return nsd == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(nsd);
    }

    @PostMapping
    public ResponseEntity<SettingsDto> createSettings(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody SettingsDto request) {
        UUID userId = UUID.fromString(JwtTokenUtils.parseJwtToken(token).get("userId").toString());
        SettingsDto sd = settingsService.createSettings(userId, request);
        return sd == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(sd);
    }

    @PutMapping
    public ResponseEntity<DefaultRs> updateSettings(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody SettingRq request) {
        UUID userId = UUID.fromString(JwtTokenUtils.parseJwtToken(token).get("userId").toString());
        if (settingsService.updateSettings(userId, request) == null) return ResponseEntity.badRequest().build();
        DefaultRs response = new DefaultRs();
        response.setTime(OffsetDateTime.now());
        response.setStatus(new MassageRs(true));
        return ResponseEntity.ok(response);
    }

}