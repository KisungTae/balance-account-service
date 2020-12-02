package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.beeswork.balanceaccountservice.entity.photo.QPhoto;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
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

    private final QAccount qAccount = QAccount.account;
    private final QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;
    private final QQuestion qQuestion = QQuestion.question;
    private final QPhoto qPhoto = QPhoto.photo;

    @Autowired
    public AccountDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Account findBy(UUID accountId, UUID identityToken) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .where(qAccount.id.eq(accountId).and(qAccount.identityToken.eq(identityToken)))
                              .fetchOne();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaQueryFactory.selectFrom(qAccount).where(qAccount.email.eq(email)).fetchCount() > 0;
    }

    @Override
    public Account findWithPhotos(UUID accountId, UUID identityToken) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .leftJoin(qAccount.photos, qPhoto)
                              .where(qAccount.id.eq(accountId).and(qAccount.identityToken.eq(identityToken)))
                              .fetchOne();
    }

    @Override
    public Account findWithAccountQuestionsIn(UUID accountId, UUID identityToken, Set<Integer> questionIds) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .leftJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                              .where(qAccount.id.eq(accountId)
                                                .and(qAccount.identityToken.eq(identityToken))
                                                .and(qAccountQuestion.selected.eq(true)
                                                                              .or(qAccountQuestion.questionId.in(questionIds))))
                              .fetchOne();
    }

    @Override
    public Account findWithAccountQuestions(UUID accountId, UUID identityToken) {
        return findWithAccountQuestions().where(qAccount.id.eq(accountId)
                                                           .and(qAccount.identityToken.eq(identityToken))
                                                           .and(qAccountQuestion.selected.eq(true)
                                                                                         .or(qAccountQuestion.accountId.isNull())))
                                         .orderBy(qAccountQuestion.sequence.asc())
                                         .fetchOne();
    }

    @Override
    public Account findWithAccountQuestions(UUID accountId) {
        return findWithAccountQuestions().where(qAccount.id.eq(accountId)
                                                           .and(qAccountQuestion.selected.eq(true)
                                                                                         .or(qAccountQuestion.accountId.isNull())))
                                         .fetchOne();
    }

    private JPAQuery<Account> findWithAccountQuestions() {
        return jpaQueryFactory.selectFrom(qAccount)
                              .leftJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                              .leftJoin(qAccountQuestion.question, qQuestion).fetchJoin();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllWithin(int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point point) {
        return entityManager.createNativeQuery(
                "select cast(b.id as varchar), b.name, b.about, b.birth_year, st_distance(b.location, :pivot), p.key, b.height " +
                "from (select * " +
                "      from account a  " +
                "      where st_dwithin(location, :pivot, :distance) " +
                "        and gender = :gender " +
                "        and birth_year <= :minAge " +
                "        and birth_year >= :maxAge " +
                "        and enabled = true" +
                "        and blocked = false " +
                "        and deleted = false " +
                "       limit :limit " +
                "       offset :offset) as b " +
                "left join photo as p " +
                "on p.account_id = b.id", Object[].class)
                            .setParameter("pivot", point)
                            .setParameter("distance", distance)
                            .setParameter("gender", gender)
                            .setParameter("minAge", minAge)
                            .setParameter("maxAge", maxAge)
                            .setParameter("limit", limit)
                            .setParameter("offset", offset)
                            .getResultList();
    }

}
