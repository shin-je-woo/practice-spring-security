package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;
    private final AtomicBoolean isResourcesConfigChanged = new AtomicBoolean(true);
    private final Map<RequestMatcher, List<String>> resourceMap = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public Map<RequestMatcher, List<String>> getResourceMap() {

        if (!isResourcesConfigChanged.get()) {
            return resourceMap;
        }
        putResourceMap();
        isResourcesConfigChanged.set(false);
        return resourceMap;
    }

    private void putResourceMap() {
        List<Resources> resources = resourcesRepository.findAllResourcesWithUrl();
        resources.forEach(resource -> {
            List<String> roleNames = resource.getRoleList().stream()
                    .map(Role::getRoleName)
                    .toList();
            resourceMap.put(new AntPathRequestMatcher(resource.getResourceName()), roleNames);
        });
    }

    public void changeResourcesConfig() {
        isResourcesConfigChanged.set(true);
    }
}
