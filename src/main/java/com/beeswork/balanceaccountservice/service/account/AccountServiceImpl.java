package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.AccountDTO;
import com.beeswork.balanceaccountservice.entity.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }


    @Override
    @Transactional
    public void save(AccountDTO accountDTO) throws AccountNotFoundException {

        Account account = accountDAO.findByIdAndName(accountDTO.getId(), accountDTO.getEmail());
        if (account == null) throw new AccountNotFoundException();

    }




}
