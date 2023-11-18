package io.security.corespringsecurity.security.listener;

import io.security.corespringsecurity.domain.entity.Account;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("=== Application 로딩 후 DATA 초기화 작업을 시작합니다. ===");
        setupSecurityResources();
    }

    public void setupSecurityResources() {
        List<Role> roles = new ArrayList<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);
        createUserIfNotFound("admin", "pass", "admin@gmail.com", 10, roles);
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
}
