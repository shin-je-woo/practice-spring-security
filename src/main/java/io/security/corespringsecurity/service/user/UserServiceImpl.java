package io.security.corespringsecurity.service.user;

import io.security.corespringsecurity.domain.entity.Account;
import io.security.corespringsecurity.domain.dto.AccountDto;
import io.security.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(AccountDto accountDto) {

        String encryptedPassword = passwordEncoder.encode(accountDto.getPassword());
        accountDto.setPassword(encryptedPassword);
        Account account = accountDto.toEntity();
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

    @Transactional
    @Override
    public void modifyUser(AccountDto accountDto) {



    }
}
