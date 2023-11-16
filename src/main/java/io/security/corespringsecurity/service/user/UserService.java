package io.security.corespringsecurity.service.user;

import io.security.corespringsecurity.domain.entity.Account;
import io.security.corespringsecurity.domain.dto.AccountDto;

import java.util.List;

public interface UserService {

    void createUser(AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void modifyUser(AccountDto accountDto);
}
