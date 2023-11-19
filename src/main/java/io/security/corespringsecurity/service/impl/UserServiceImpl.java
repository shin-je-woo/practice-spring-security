package io.security.corespringsecurity.service.impl;

import io.security.corespringsecurity.domain.dto.AccountDto;
import io.security.corespringsecurity.domain.entity.Account;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public void createUser(AccountDto accountDto) {

        String encryptedPassword = passwordEncoder.encode(accountDto.getPassword());
        accountDto.setPassword(encryptedPassword);
        Account account = accountDto.toEntity();
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("USER권한이 존재하지 않습니다."));
        account.getUserRoles().add(userRole);

        userRepository.save(account);
    }

    @Override
    public List<Account> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public AccountDto getUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id = " + id))
                .toDto();
    }

    /**
     * JPA dirty checking으로 회원정보 수정
     */
    @Transactional
    @Override
    public void modifyUser(AccountDto accountDto) {

        Account account = userRepository.findById(accountDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id = " + accountDto.getId()));

        account.getUserRoles().clear();
        accountDto.getRoles().forEach(r -> {
            Role role = roleRepository.findByRoleName(r)
                    .orElseThrow(() -> new IllegalArgumentException("권한이름이 존재하지 않습니다. roleName = " + r));
            account.getUserRoles().add(role);
        });

        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
    }
}


