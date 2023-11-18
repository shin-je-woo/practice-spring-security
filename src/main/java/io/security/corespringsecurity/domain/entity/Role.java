package io.security.corespringsecurity.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    private String roleName;

    private String roleDesc;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userRoles")
    private List<Account> accounts = new ArrayList<>();

    @Builder
    public Role(Long id, String roleName, String roleDesc, List<Account> accounts) {
        this.id = id;
        this.roleName = roleName;
        this.roleDesc = roleDesc;
        this.accounts = accounts;
    }
}
