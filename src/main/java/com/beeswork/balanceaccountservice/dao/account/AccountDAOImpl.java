package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QPhoto;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
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
    public List<Account> findAllWithin(UUID accountId, int distance, int minAge, int maxAge, boolean showMe, int index, Point point)
    throws AccountNotFoundException {

        Account account = findById(accountId);

        Session session = entityManager.unwrap(Session.class);

        session.createQuery("from Account as a where st_within(a.location, ) ")


        return null;
    }


}
