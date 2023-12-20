package io.security.corespringsecurity.security.handler.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.security.corespringsecurity.config.JwtProperties;
import io.security.corespringsecurity.security.service.AccountContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JwtPublisher implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AccountContext accountContext = (AccountContext) authentication.getPrincipal();

        String jws = Jwts.builder()
                .header()
                .add("typ", "jwlog-JWT")
                .and()
                .claim("name", accountContext.getAccount().getUsername())
                .claim("auth", accountContext.getAuthorities())
                .signWith(jwtProperties.getSignatureKey())
                .compact();
        String jwsJson = objectMapper.writeValueAsString(
                new Object() {
                    @Getter
                    private final String token = jws;
                });

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.getWriter().write(jwsJson);
    }
}
