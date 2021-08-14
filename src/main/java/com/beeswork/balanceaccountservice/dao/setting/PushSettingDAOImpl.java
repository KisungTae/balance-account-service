package com.beeswork.balanceaccountservice.dao.setting;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.entity.setting.QPushSetting;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class PushSettingDAOImpl extends BaseDAOImpl<PushSetting> implements PushSettingDAO {

    private final QPushSetting qPushSetting = QPushSetting.pushSetting;

    public PushSettingDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public PushSetting findByAccountId(UUID accountId) {
        return jpaQueryFactory.selectFrom(qPushSetting).where(qPushSetting.accountId.eq(accountId)).fetchFirst();
    }
}
