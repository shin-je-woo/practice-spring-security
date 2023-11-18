package io.security.corespringsecurity.domain.dto;

import io.security.corespringsecurity.domain.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {

    private Long id;
    private String roleName;
    private String roleDesc;

    @Builder
    public RoleDto(Long id, String roleName, String roleDesc) {
        this.id = id;
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    public Role toEntity() {
        return Role.builder()
                .roleName(roleName)
                .roleDesc(roleDesc)
                .build();
    }
}
