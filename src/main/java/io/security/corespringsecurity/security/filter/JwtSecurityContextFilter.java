package io.security.corespringsecurity.security.filter;

import io.jsonwebtoken.JwtException;
import io.security.corespringsecurity.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtSecurityContextFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Authentication authentication = resolveToken(httpServletRequest, httpServletResponse);
        SecurityContext securityContext = securityContextHolderStrategy.getContext();
        securityContext.setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private Authentication resolveToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = null;
        String jws = request.getHeader(AUTHORIZATION_HEADER);
        try {
             authentication= jwtProvider.getAuthentication(jws);
        } catch (JwtException e) {
            log.error("don't trust the JWT!");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
        }
        return authentication;
    }
}
