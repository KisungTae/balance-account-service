package com.beeswork.balanceaccountservice.dao.setting;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.entity.setting.QSetting;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class PushSettingDAOImpl extends BaseDAOImpl<PushSetting> implements PushSettingDAO {

    private final QSetting qSetting = QSetting.setting;

    public PushSettingDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public PushSetting findByAccountId(UUID accountId) {
        return jpaQueryFactory.selectFrom(qSetting).where(qSetting.accountId.eq(accountId)).fetchFirst();
    }
}
