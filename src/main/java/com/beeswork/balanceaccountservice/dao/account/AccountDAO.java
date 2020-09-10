package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findById(UUID id) throws AccountNotFoundException;
    List<Account> findAllWithin(UUID accountId, int distance, int minAge, int maxAge, boolean showMe, int index, Point point)
    throws AccountNotFoundException;
//    Account findByIdAndName(long id, String email);
}
