package skillbox.notification.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import skillbox.notification.dto.AccountDataDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final RestTemplate restTemplate;
    private static final String ACCOUNT_SERVICE_URL = "http://mc-account:8080/api/v1/account/{id}";

    public AccountDataDto getAccount(UUID userId) {
        try {
            log.info("Fetching account for userId: {}", userId);
            AccountDataDto account = restTemplate.getForObject(
                    ACCOUNT_SERVICE_URL,
                    AccountDataDto.class,
                    userId
            );
            if (account == null) {
                log.warn("Account not found for userId: {}", userId);
                return new AccountDataDto("Unknown", "User", "");
            }
            log.info("Account fetched successfully for userId: {}", userId);
            return new AccountDataDto(
                    account.getFirstName() != null ? account.getFirstName() : "Unknown",
                    account.getLastName() != null ? account.getLastName() : "User",
                    account.getPhoto() != null ? account.getPhoto() : ""
            );
        } catch (Exception e) {
            log.error("Failed to fetch account for userId {}: {}", userId, e.getMessage());
            return new AccountDataDto("Unknown", "User", "");
        }
    }
}