package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findById(UUID id) throws AccountNotFoundException;
    List<Object[]> findAllWithin(UUID accountId, int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point point)
    throws AccountNotFoundException;
    Account findByIdWithQuestions(UUID accountId) throws AccountNotFoundException;
    Account findByIdWithAccountQuestions(UUID accountId) throws AccountNotFoundException;
    boolean existsByIdAndEmail(UUID id, String email);
}
