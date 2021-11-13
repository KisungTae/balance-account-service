package com.beeswork.balanceaccountservice.service.security;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    private final AccountDAO accountDAO;

    @Autowired
    public UserDetailServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public UserDetails loadValidUserByUsername(UUID userName, UUID identityToken) {
        if (userName == null || identityToken == null) throw new AccountNotFoundException();
        Account account = accountDAO.findById(userName);
        if (account == null) throw new AccountNotFoundException();
        account.validate(identityToken);
        return account;
    }
}
