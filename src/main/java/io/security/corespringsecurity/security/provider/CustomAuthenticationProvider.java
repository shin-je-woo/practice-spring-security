package io.security.corespringsecurity.security.provider;

import io.security.corespringsecurity.security.common.FormWebAuthenticationDetails;
import io.security.corespringsecurity.security.service.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * AuthenticationManager로부터 인증처리를 위임 받는다.
     *
     * @param authentication AuthenticationManager가 전달해준 authentication. 인증요청한 id와 pw 포함.
     */
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

        if (!(authentication.getDetails() instanceof FormWebAuthenticationDetails webAuthenticationDetails))
            throw new IllegalStateException("FormWebAuthenticationDetails를 등록해주세요.");

        // 요청과 WebAuthenticationDetails의 secretKey가 일치하는지 확인
        if (!"secret".equals(webAuthenticationDetails.getSecretKey()))
            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");

        return new UsernamePasswordAuthenticationToken(accountContext, null, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
