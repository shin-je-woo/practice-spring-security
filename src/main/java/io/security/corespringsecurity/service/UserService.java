package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.dto.AccountDto;
import io.security.corespringsecurity.domain.entity.Account;

import java.util.List;

public interface UserService {

    void createUser(AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void modifyUser(AccountDto accountDto);

    void secure();
}
