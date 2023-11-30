package io.security.corespringsecurity.config;

import io.security.corespringsecurity.repository.ResourcesRepository;
import io.security.corespringsecurity.service.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    protected SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository) {
        return new SecurityResourceService(resourcesRepository);
    }
}
