package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.QPhoto;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
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
    private final QPhoto qPhoto = QPhoto.photo;
    private final QMatch qMatch = QMatch.match;

    @Autowired
    public AccountDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Account findById(UUID id) throws AccountNotFoundException {
        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(id)).fetchOne();
        if (account == null) throw new AccountNotFoundException();
        return account;
    }

    @Override
    public Account findByIdWithAccountQuestions(UUID accountId) throws AccountNotFoundException {
        Account account = jpaQueryFactory.selectFrom(qAccount)
                                         .innerJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                                         .where(qAccount.id.eq(accountId))
                                         .fetchOne();

        if (account == null) throw new AccountNotFoundException();
        return account;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllWithin(UUID accountId, int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point point) {


        
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

    @Override
    public Account findByIdWithQuestions(UUID accountId) throws AccountNotFoundException {

        Account account = jpaQueryFactory.selectFrom(qAccount)
                                         .innerJoin(qAccount.accountQuestions, qAccountQuestion).fetchJoin()
                                         .innerJoin(qAccountQuestion.question, qQuestion).fetchJoin()
                                         .where(qAccount.id.eq(accountId))
                                         .fetchOne();

        if (account == null) throw new AccountNotFoundException();
        return account;
    }


}
