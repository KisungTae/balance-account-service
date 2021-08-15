package com.beeswork.balanceaccountservice.service.security;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailServiceImpl extends BaseServiceImpl implements UserDetailsService {

    private final AccountDAO accountDAO;

    @Autowired
    public CustomUserDetailServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountDAO.findById(UUID.fromString(userName));
        validateAccount(account);
//        account.getAuthorities();
        return account;
    }
}
