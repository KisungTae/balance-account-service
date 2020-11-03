package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findById(UUID accountId);
    Account findBy(UUID accountId, String email);
    Account findWithQuestions(UUID accountId, String email);
    Account findWithQuestions(UUID accountId);
    Account findWithAccountQuestions(UUID accountId, String email);
    List<Object[]> findAllWithin(int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point point);
    boolean existsBy(UUID accountId, String email, boolean blocked);
}
