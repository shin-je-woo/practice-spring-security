package io.security.corespringsecurity.security.listener;

import io.security.corespringsecurity.domain.entity.Account;
import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.ResourcesRepository;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResourcesRepository resourcesRepository;
    private final PasswordEncoder passwordEncoder;

    private static AtomicInteger resourceOrder = new AtomicInteger(0);

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("=== Application 로딩 후 DATA 초기화 작업을 시작합니다. ===");
        setupSecurityResources();
    }

    public void setupSecurityResources() {
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        Role userRole = createRoleIfNotFound("ROLE_USER", "사용자");
        createUserIfNotFound("admin", "pass", "admin@gmail.com", 10, List.of(userRole, managerRole, adminRole));
        createUserIfNotFound("user", "pass", "user@gmail.com", 20, List.of(userRole));
        createResourceIfNotFound("/admin/**", "", List.of(adminRole), "url");
    }

    private Role createRoleIfNotFound(String roleName, String roleDesc) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> Role.builder()
                        .roleName(roleName)
                        .roleDesc(roleDesc)
                        .build());

        return roleRepository.save(role);
    }

    private Account createUserIfNotFound(String userName, String password, String email, int age, List<Role> roles) {

        Account account = userRepository.findByUsername(userName)
                .orElseGet(() -> Account.builder()
                        .username(userName)
                        .password(passwordEncoder.encode(password))
                        .email(email)
                        .age(age)
                        .userRoles(roles)
                        .build());

        return userRepository.save(account);
    }

    private Resources createResourceIfNotFound(String resourceName, String httpMethod, List<Role> roleList, String resourceType) {

        Resources resources = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod)
                .orElseGet(() -> Resources.builder()
                        .resourceName(resourceName)
                        .httpMethod(httpMethod)
                        .resourceType(resourceType)
                        .orderNum(resourceOrder.incrementAndGet())
                        .roleList(roleList)
                        .build());

        return resourcesRepository.save(resources);
    }
}
