package io.security.corespringsecurity.service.impl;

import io.security.corespringsecurity.domain.dto.RoleDto;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleDto getRole(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("권한이 존재하지 않습니다. id = " + id))
                .toDto();
    }

    @Transactional
    @Override
    public void createRole(RoleDto roleDto) {
        Role role = roleDto.toEntity();
        roleRepository.save(role);
    }
}
