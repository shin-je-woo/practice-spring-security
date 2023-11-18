package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.dto.RoleDto;
import io.security.corespringsecurity.domain.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();

    RoleDto getRole(long id);

    void createRole(RoleDto roleDto);
}
