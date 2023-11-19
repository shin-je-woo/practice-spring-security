package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.dto.ResourcesDto;

import java.util.List;

public interface ResourcesService {

    List<ResourcesDto> getResources();

    ResourcesDto getResources(long id);

    void createResources(ResourcesDto resourcesDto);

    void deleteResoureces(Long id);
}
