package com.beeswork.balanceaccountservice.dao.wallet;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.QWallet;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.UUID;

@Repository
public class WalletDAOImpl extends BaseDAOImpl<Wallet> implements WalletDAO {

    private final QWallet qWallet = QWallet.wallet;

    public WalletDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Wallet findByAccountId(UUID accountId) {
        return jpaQueryFactory.selectFrom(qWallet).where(qWallet.accountId.eq(accountId)).fetchFirst();
    }
}
