package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findById(UUID accountId);
    Account findBy(UUID accountId, UUID identityToken);
    boolean existsByEmail(String email);
    Account findWithPhotos(UUID accountId, UUID identityToken);
    Account findWithAccountQuestions(UUID accountId, UUID identityToken);
    Account findWithAccountQuestions(UUID accountId);
    Account findWithAccountQuestionsIn(UUID accountId, UUID identityToken, Set<Integer> questionIds);
    Account findWithPhotosAndAccountQuestions(UUID accountId, UUID identityToken);
}
