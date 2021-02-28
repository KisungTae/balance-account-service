package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.beeswork.balanceaccountservice.entity.photo.QPhoto;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class AccountDAOImpl extends BaseDAOImpl<Account> implements AccountDAO {

    private final QAccount         qAccount         = QAccount.account;
    private final QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;
    private final QQuestion        qQuestion        = QQuestion.question;
    private final QPhoto           qPhoto           = QPhoto.photo;

    @Autowired
    public AccountDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Account findById(UUID accountId) {
        return entityManager.find(Account.class, accountId);
    }

    public Account findWithPhotosAndAccountQuestions(UUID accountId, UUID identityToken) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .leftJoin(qAccount.photos, qPhoto).fetchJoin()
                              .leftJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                              .where(qAccount.id.eq(accountId).and(qAccount.identityToken.eq(identityToken)))
                              .fetchOne();
    }

    @Override
    public Account findWithPhotos(UUID accountId, UUID identityToken) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .leftJoin(qAccount.photos, qPhoto)
                              .fetchJoin()
                              .where(qAccount.id.eq(accountId).and(qAccount.identityToken.eq(identityToken)))
                              .fetchOne();
    }

    @Override
    public Account findWithAccountQuestions(UUID accountId, UUID identityToken) {
        return findWithAccountQuestions().where(qAccount.id.eq(accountId)
                                                           .and(qAccount.identityToken.eq(identityToken))
                                                           .and(qAccountQuestion.selected.eq(true)
                                                                                         .or(qAccountQuestion.accountQuestionId.accountId
                                                                                                     .isNull())))
                                         .orderBy(qAccountQuestion.sequence.asc())
                                         .fetchOne();
    }

    private JPAQuery<Account> findWithAccountQuestions() {
        return jpaQueryFactory.selectFrom(qAccount)
                              .leftJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                              .leftJoin(qAccountQuestion.question, qQuestion).fetchJoin();
    }


}
