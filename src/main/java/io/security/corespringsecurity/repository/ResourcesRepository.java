package io.security.corespringsecurity.repository;

import io.security.corespringsecurity.domain.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    Optional<Resources> findByResourceNameAndHttpMethod(String resourceName, String httpMethod);
}
