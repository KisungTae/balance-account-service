package com.beeswork.balanceaccountservice.dao.login;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.login.RefreshToken;

import java.util.UUID;

public interface RefreshTokenDAO extends BaseDAO<RefreshToken> {
    RefreshToken findByAccountId(UUID accountId);
}
