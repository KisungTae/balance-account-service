package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.constant.QueryParameter;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountType;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QPhoto;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountDAOImpl extends BaseDAOImpl<Account> implements AccountDAO {

    private final QAccount qAccount = QAccount.account;
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
    public List<Account> findAllWithin(UUID accountId, int distance, int minAge, int maxAge, boolean gender, int index, Point point) {

        Session session = entityManager.unwrap(Session.class);

//        entityManager.createQuery("select a.id from Account a where dwithin(a.location, :pivot, :distance) = true ");


        javax.persistence.Query query = entityManager.createNativeQuery("select cast(b.id as varchar) " +
                                                              "from (select * " +
                                                              "      from account a  " +
                                                              "      where st_dwithin(location, st_setsrid(st_point(126.807883, 37.521757), 4326), 5000) " +
                                                              "        and gender = true " +
                                                              "        and birth_year >= 1970 " +
                                                              "        and birth_year <= 2003 " +
                                                              "        and enabled = true " +
                                                              "      limit 30 offset 0) as b " +
                                                              "inner join photo as p " +
                                                              "on p.account_id = b.id");


//        javax.persistence.Query query = entityManager.createNativeQuery("select * from account_type");


//        Query query = session.createQuery("from Account as a where dwithin(a.location, :pivot, :distance) = true " +
//                                                  "and a.gender = :gender " +
//                                                  "and (a.birthYear >= :minAge and a.birthYear <= :maxAge) " +
//                                                  "and a.enabled = true")
//                             .setParameter("pivot", point)
//                             .setParameter("distance", distance)
//                             .setParameter("gender", gender)
//                             .setParameter("minAge", minAge)
//                             .setParameter("maxAge", maxAge)
//                             .setFirstResult((index * QueryParameter.LIMIT))
//                             .setMaxResults(QueryParameter.LIMIT);

        List list = query.getResultList();

        for (Object o : list) {
            AccountType account = (AccountType) o;
            System.out.println(account.getId());
        }

        System.out.println();

        return null;
    }


}
