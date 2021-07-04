package com.beeswork.balanceaccountservice.dao.login;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.login.LoginId;

import java.util.UUID;

public interface LoginDAO extends BaseDAO<Login> {
    Login findById(LoginId loginId);
    Login findByAccountId(UUID accountId);
    boolean existsByEmail(String email);
}
