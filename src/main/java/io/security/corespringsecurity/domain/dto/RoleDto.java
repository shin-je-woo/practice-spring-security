package io.security.corespringsecurity.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {

    private String id;
    private String roleName;
    private String roleDesc;
}
