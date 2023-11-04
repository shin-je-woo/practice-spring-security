package io.security.corespringsecurity.service.user;

import io.security.corespringsecurity.domain.Account;

public interface UserService {

    void createUser(Account account);
}
