package com.beeswork.balanceaccountservice.dao.login;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.login.RefreshToken;

import java.util.UUID;

public interface RefreshTokenDAO extends BaseDAO<RefreshToken> {
    boolean existsByAccountIdAndKey(UUID accountId, UUID key);
    RefreshToken findRecentByAccountId(UUID accountId);
    RefreshToken findByAccountId(UUID accountId);

}
