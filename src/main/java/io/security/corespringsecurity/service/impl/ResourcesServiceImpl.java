package io.security.corespringsecurity.service.impl;

import io.security.corespringsecurity.domain.dto.ResourcesDto;
import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.ResourcesRepository;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.service.ResourcesService;
import io.security.corespringsecurity.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourcesServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;
    private final RoleRepository roleRepository;
    private final SecurityResourceService securityResourceService;

    @Override
    public List<ResourcesDto> getResources() {
        List<Resources> resources = resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
        return resources.stream()
                .map(Resources::toDto)
                .toList();
    }

    @Override
    public ResourcesDto getResources(long id) {
        return resourcesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자원을 찾을 수 없습니다. id = " + id))
                .toDto();
    }

    @Transactional
    @Override
    public void createResources(ResourcesDto resourcesDto) {

        Resources resources = resourcesDto.toEntity();
        Role role = roleRepository.findByRoleName(resourcesDto.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("해당 권한이 존재하지 않습니다. roleName = " + resourcesDto.getRoleName()));
        resources.getRoleList().add(role);

        resourcesRepository.save(resources);
        securityResourceService.changeResource();
    }

    @Transactional
    @Override
    public void deleteResoureces(Long id) {
        resourcesRepository.deleteById(id);
        securityResourceService.changeResource();
    }
}
