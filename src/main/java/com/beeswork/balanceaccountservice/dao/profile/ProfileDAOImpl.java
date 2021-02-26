package com.beeswork.balanceaccountservice.dao.profile;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.account.QProfile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class ProfileDAOImpl extends BaseDAOImpl<Profile> implements ProfileDAO {

    private final QProfile qProfile = QProfile.profile;

    @Autowired
    public ProfileDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Profile findById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qProfile).where(qProfile.accountId.eq(accountId)).fetchOne();
    }

    @Override
    public boolean existsById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qProfile).where(qProfile.accountId.eq(accountId)).fetchCount() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllWithin(int distance,
                                        int minAge,
                                        int maxAge,
                                        boolean gender,
                                        int limit,
                                        int offset,
                                        Point point) {
        return entityManager.createNativeQuery(
                "select cast(b.account_id as varchar), b.name, b.about, b.birth_year, st_distance(b.location, :pivot), p.key, b.height " +
                "from (select * " +
                "      from profile  " +
                "      where st_dwithin(location, :pivot, :distance) " +
                "        and gender = :gender " +
                "        and birth_year <= :minAge " +
                "        and birth_year >= :maxAge " +
                "        and enabled = true " +
                "        and deleted = false " +
                "       limit :limit " +
                "       offset :offset) as b " +
                "left join photo as p " +
                "on p.account_id = b.account_id")
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
