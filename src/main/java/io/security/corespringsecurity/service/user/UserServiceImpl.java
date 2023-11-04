package io.security.corespringsecurity.service.user;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.domain.AccountDto;
import io.security.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
