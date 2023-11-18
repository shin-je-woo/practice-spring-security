package io.security.corespringsecurity.domain.entity;

import io.security.corespringsecurity.domain.dto.AccountDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private String username;

    @Setter
    private String password;

    private String email;

    private int age;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "account_roles",
            joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> userRoles = new ArrayList<>();

    @Builder
    public Account(Long id, String username, String password, String email, int age, List<Role> userRoles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.userRoles = userRoles != null ? userRoles : new ArrayList<>();
    }

    public AccountDto toDto() {
        AccountDto accountDto = AccountDto.builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .age(age)
                .build();

        List<String> roleNames = userRoles.stream()
                .map(Role::getRoleName)
                .toList();
        accountDto.setRoles(roleNames);

        return accountDto;
    }
}
