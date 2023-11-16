package io.security.corespringsecurity.domain.dto;

import io.security.corespringsecurity.domain.entity.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String age;
    private List<String> roles;

    @Builder
    public AccountDto(Long id, String username, String password, String email, String age, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.roles = roles;
    }

    public Account toEntity() {
        return Account.builder()
                .username(username)
                .password(password)
                .email(email)
                .age(age)
                .build();
    }
}
