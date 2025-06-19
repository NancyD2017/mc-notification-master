package skillbox.notification.utils;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@UtilityClass
public class JwtTokenUtils {

    @SneakyThrows
    public Map<String, Object> parseJwtToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid bearer token format");
        }
        String token = bearerToken.replaceFirst("Bearer ", "");
        JWT jwt = JWTParser.parse(token);
        return jwt.getJWTClaimsSet().getClaims();
    }

    @Profile("test")
    public Map<String, Object> parseJwtTokenForTest(String bearerToken, Map<String, Object> mockClaims) {
        return mockClaims;
    }
}
