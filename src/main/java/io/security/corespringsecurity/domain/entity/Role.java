package io.security.corespringsecurity.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
}
