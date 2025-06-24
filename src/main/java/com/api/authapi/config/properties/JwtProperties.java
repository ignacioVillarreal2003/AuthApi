package com.api.authapi.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Expiration expiration;

    @Getter
    @Setter
    public static class Expiration {
        private long accessTokenMs;
        private long refreshTokenMs;
    }
}
