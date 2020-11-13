package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findBy(UUID accountId, UUID identityToken);
    Account findWithPhotos(UUID accountId, UUID identityToken);
    Account findWithQuestions(UUID accountId, UUID identityToken);
    Account findWithQuestions(UUID accountId);
    Account findWithAccountQuestionsWithQuestionIdIn(UUID accountId, UUID identityToken, List<Long> questionIds);
    List<Object[]> findAllWithin(int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point point);
}
