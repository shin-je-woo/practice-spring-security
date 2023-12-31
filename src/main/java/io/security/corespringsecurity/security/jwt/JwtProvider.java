package io.security.corespringsecurity.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.security.corespringsecurity.domain.dto.AccountDto;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.token.AjaxAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.security.corespringsecurity.security.jwt.JwtClaims.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String createToken(Authentication authentication) {
        AccountContext principal = (AccountContext) authentication.getPrincipal();
        String authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder().header()
                .add(TYPE.getKey(), "jwlog-jwt")
                .and()
                .claim(USER_ID.getKey(), principal.getAccount().getId())
                .claim(USER_NAME.getKey(), principal.getAccount().getUsername())
                .claim(EMAIL.getKey(), principal.getAccount().getEmail())
                .claim(AUTH.getKey(), authorities)
                .signWith(jwtProperties.getSignatureKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith((SecretKey) jwtProperties.getSignatureKey())
                .build()
                .parseSignedClaims(token);
        Claims payload = claimsJws.getPayload();

        String[] authList = payload.get(AUTH.getKey()).toString().split(",");
        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(authList)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        AccountDto accountDto = AccountDto.builder()
                .id(Long.valueOf(getClaimValue(payload, USER_ID).toString()))
                .username(getClaimValue(payload, USER_NAME).toString())
                .password("")
                .roles(Arrays.stream(authList).toList())
                .email(getClaimValue(payload, EMAIL).toString())
                .build();

        AccountContext accountContext = new AccountContext(accountDto, authorities);

        return new AjaxAuthenticationToken(accountContext, null, authorities);
    }

    private Object getClaimValue(Claims claims, JwtClaims jwtClaims) {
        return claims.get(jwtClaims.getKey());
    }
}
