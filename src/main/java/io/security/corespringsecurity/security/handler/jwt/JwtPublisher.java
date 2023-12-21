package io.security.corespringsecurity.security.handler.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.security.jwt.JwtProvider;
import io.security.corespringsecurity.security.jwt.response.JwtResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JwtPublisher implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String jws = jwtProvider.createToken(authentication);
        JwtResponseDto jwtResponse = JwtResponseDto.builder()
                .accessToken(jws)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.getWriter().write(objectMapper.writeValueAsString(jwtResponse));
    }
}
