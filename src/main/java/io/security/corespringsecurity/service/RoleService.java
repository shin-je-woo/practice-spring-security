package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();
}
