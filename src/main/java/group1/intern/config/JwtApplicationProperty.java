package group1.intern.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
@Getter
public class JwtApplicationProperty {
    @Value("${application.auth.access-token-secret-key}")
    private String accessTokenSecret;
    @Value("${application.auth.access-token-expiration-ms}")
    private Long accessTokenExpirationMs;
    @Value("${application.auth.refresh-token-secret-key}")
    private String refreshTokenSecret;
    @Value("${application.auth.refresh-token-expiration-ms}")
    private Long refreshTokenExpirationMs;
}
