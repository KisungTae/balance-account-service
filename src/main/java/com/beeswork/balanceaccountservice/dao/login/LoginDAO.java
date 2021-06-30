package com.beeswork.balanceaccountservice.dao.login;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.login.Login;

import java.util.UUID;

public interface LoginDAO extends BaseDAO<Login> {
    Login findById(String id);
    Login findByAccountId(UUID accountId);
    boolean existsByEmail(String email);
}
