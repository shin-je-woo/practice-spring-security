package io.security.corespringsecurity.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtClaims {

    TYPE("typ"),
    USER_ID("uid"),
    USER_NAME("unm"),
    EMAIL("eml"),
    AUTH("ath");

    private final String key;
}
