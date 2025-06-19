package skillbox.notification.dto;

import lombok.*;
import skillbox.notification.model.StatusCode;

import java.time.OffsetDateTime;

@Data
public class AccountDto {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private StatusCode statusCode;
    private OffsetDateTime birthDate;
    private String messagePermission;
    private OffsetDateTime lastOnlineTime;
    private String emojiStatus;
    private boolean deleted;
    private boolean blocked;
    private boolean isOnline;
}