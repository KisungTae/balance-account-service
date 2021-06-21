package com.beeswork.balanceaccountservice.dao.setting;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.setting.QSetting;
import com.beeswork.balanceaccountservice.entity.setting.Setting;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class SettingDAOImpl extends BaseDAOImpl<Setting> implements SettingDAO {

    private final QSetting qSetting = QSetting.setting;

    public SettingDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Setting findById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qSetting).where(qSetting.accountId.eq(accountId)).fetchFirst();
    }
}
