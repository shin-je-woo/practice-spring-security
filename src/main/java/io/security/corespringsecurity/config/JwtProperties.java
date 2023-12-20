package io.security.corespringsecurity.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.Key;
import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "jwlog-jwt")
@RequiredArgsConstructor
public class JwtProperties {

    @PostConstruct
    public void init() {
        this.signatureKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(signatureKeyString));
    }

    private final String signatureKeyString;

    private Key signatureKey;
}
