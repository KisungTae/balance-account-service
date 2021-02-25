package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Profile;

import java.util.UUID;

public interface ProfileDAO extends BaseDAO<Profile> {

    Profile findById(UUID accountId);
}
