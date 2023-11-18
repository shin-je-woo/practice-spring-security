package io.security.corespringsecurity.security.service;

import io.security.corespringsecurity.domain.dto.AccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AccountContext extends User {

    private final AccountDto account;

    public AccountContext(AccountDto account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }
}
