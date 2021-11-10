package com.beeswork.balanceaccountservice.service.security;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountDeletedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.util.Convert;
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
    public UserDetails loadUserByUsername(UUID userName, UUID identityToken) {
        Account account = accountDAO.findById(userName);
        validateAccount(account, identityToken);
        return account;
    }

    private void validateAccount(Account account, UUID identityToken) {
        if (account == null) throw new AccountNotFoundException();
        if (account.isBlocked()) throw new AccountBlockedException();
        if (account.isDeleted()) throw new AccountDeletedException();
        if (!account.getIdentityToken().equals(identityToken)) throw new AccountNotFoundException();
    }
}
