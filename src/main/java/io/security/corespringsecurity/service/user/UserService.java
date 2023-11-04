package io.security.corespringsecurity.service.user;

import io.security.corespringsecurity.domain.AccountDto;

public interface UserService {

    void createUser(AccountDto accountDto);
}
