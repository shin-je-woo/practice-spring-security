package io.security.corespringsecurity.security.provider;

import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.token.AjaxAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!(userDetails instanceof AccountContext accountContext))
            throw new IllegalStateException("CustomUserDetailsService를 등록해주세요.");

        // 사용자가 인증요청한 패스워드와 사용자의 PW(DB에 저장)와 다르면 exception
        if (!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        return new AjaxAuthenticationToken(accountContext, null, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(AjaxAuthenticationToken.class);
    }
}
