package io.security.corespringsecurity.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class FormAccessDeniedHandler implements AccessDeniedHandler {

    private final String deniedUrl;

    public FormAccessDeniedHandler(String deniedUrl) {
        this.deniedUrl = deniedUrl;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendRedirect(deniedUrl + "?exception=" + accessDeniedException.getMessage());
    }
}
