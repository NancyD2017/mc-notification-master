package skillbox.notification;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.UUID;

public class JwtTestUtils {
    private static final String SECRET_KEY = "your-256-bit-secret-key-for-tests-1234567890abcdef";
    private static final long EXPIRATION_TIME_MS = 3600_000;

    public static String generateTokenWithUserId(UUID userId) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("userId", userId.toString())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            MACSigner signer = new MACSigner(SECRET_KEY);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }
}