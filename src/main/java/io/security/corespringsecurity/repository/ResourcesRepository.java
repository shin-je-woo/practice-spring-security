package io.security.corespringsecurity.repository;

import io.security.corespringsecurity.domain.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    Optional<Resources> findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @Query("select r from Resources r join fetch r.roleList where r.resourceType = 'url' order by r.orderNum")
    List<Resources> findAllResourcesWithUrl();
}
