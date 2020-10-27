package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.photo.QPhoto;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountDAOImpl extends BaseDAOImpl<Account> implements AccountDAO {

    private final QAccount qAccount = QAccount.account;
    private final QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;
    private final QQuestion qQuestion = QQuestion.question;

    @Autowired
    public AccountDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

//  TODO: removeme
    @Override
    public Account findById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(accountId)).fetchOne();
    }

    @Override
    public Account findBy(UUID accountId, String email) {
        return jpaQueryFactory.selectFrom(qAccount).where(validAccount(accountId, email)).fetchOne();
    }

    @Override
    public Account findWithAccountQuestions(UUID accountId, String email) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .innerJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                              .where(validAccount(accountId, email))
                              .fetchOne();
    }

    @Override
    public Account findWithQuestions(UUID accountId, String email) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .innerJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                              .innerJoin(qAccountQuestion.question, qQuestion).fetchJoin()
                              .where(validAccount(accountId, email))
                              .fetchOne();
    }

    @Override
    public boolean existsBy(UUID accountId, String email, boolean blocked) {
        return jpaQueryFactory.selectFrom(qAccount)
                              .where(qAccount.id.eq(accountId).and(qAccount.email.eq(email)).and(qAccount.blocked.eq(blocked)))
                              .fetchCount() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllWithin(int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point point) {
        return entityManager.createNativeQuery(
                "select cast(b.id as varchar), b.name, b.about, b.birth_year, st_distance(b.location, :pivot), p.key " +
                "from (select * " +
                "      from account a  " +
                "      where st_dwithin(location, :pivot, :distance) " +
                "        and gender = :gender " +
                "        and birth_year <= :minAge " +
                "        and birth_year >= :maxAge " +
                "        and enabled = true" +
                "        and blocked = false " +
                "       limit :limit " +
                "       offset :offset) as b " +
                "inner join photo as p " +
                "on p.account_id = b.id")
                            .setParameter("pivot", point)
                            .setParameter("distance", distance)
                            .setParameter("gender", gender)
                            .setParameter("minAge", minAge)
                            .setParameter("maxAge", maxAge)
                            .setParameter("limit", limit)
                            .setParameter("offset", offset)
                            .getResultList();
    }

    private BooleanBuilder validAccount(UUID accountId, String email) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(qAccount.id.eq(accountId));
        where.and(qAccount.blocked.eq(true));
        if (email != null) where.and(qAccount.email.eq(email));
        return where;
    }


}
