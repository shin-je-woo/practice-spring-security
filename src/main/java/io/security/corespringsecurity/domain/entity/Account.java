package io.security.corespringsecurity.domain.entity;

import io.security.corespringsecurity.domain.dto.AccountDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private String username;

    private String password;

    private String email;

    private String age;

    @Builder
    public Account(Long id, String username, String password, String email, String age, Set<String> userRoles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
//        this.userRoles = userRoles;
    }

    public AccountDto toDto() {
        return AccountDto.builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .age(age)
//                .roles(userRoles.stream().toList())
                .build();
    }
}
