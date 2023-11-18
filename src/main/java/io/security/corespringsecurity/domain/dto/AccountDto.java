package io.security.corespringsecurity.domain.dto;

import io.security.corespringsecurity.domain.entity.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String username;
    private String password;
    private String email;
    private int age;
    private List<String> roles = new ArrayList<>();

    @Builder
    public AccountDto(Long id, String username, String password, String email, int age, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.roles = roles;
    }

    /**
     * roles는 직접 변환 필요
     */
    public Account toEntity() {
        return Account.builder()
                .username(username)
                .password(password)
                .email(email)
                .age(age)
                .build();
    }
}
