package com.beeswork.balanceaccountservice.dao.profile;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTOResultTransformer;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.profile.QProfile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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
        return jpaQueryFactory.selectFrom(qProfile).where(qProfile.accountId.eq(accountId)).fetchFirst();
    }

    @Override
    public Profile findByIdWithLock(UUID accountId) {
        return entityManager.find(Profile.class, accountId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CardDTO findCardDTO(UUID swipedId, Point pivot) {
        List<Object[]> rows = entityManager.createNativeQuery(
                "select cast(pr.account_id as varchar), pr.name, pr.birth_year, pr.gender, pr.height, pr.about, st_distance(pr.location, :pivot), ph.key " +
                "from profile pr " +
                "left join photo ph on pr.account_id = ph.account_id " +
                "where pr.account_id = :swipedId ")
                                           .setParameter("swipedId", swipedId)
                                           .setParameter("pivot", pivot)
                                           .getResultList();
        return CardDTOResultTransformer.map(rows);
    }

    @Override
    public boolean existsById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qProfile).where(qProfile.accountId.eq(accountId)).fetchCount() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CardDTO> findCardDTOs(int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point pivot) {
        List<Object[]> rows = entityManager.createNativeQuery(
                "select cast(b.account_id as varchar) as id, b.name, b.birth_year, b.height, b.about, st_distance(b.location, :pivot), p.key " +
                "from (select * " +
                "      from profile  " +
                "      where st_dwithin(location, :pivot, :distance) " +
                "        and gender = :gender " +
                "        and birth_year <= :minAge " +
                "        and birth_year >= :maxAge " +
                "        and enabled = true " +
                "        order by score " +
                "       limit :limit " +
                "       offset :offset) as b " +
                "left join photo as p " +
                "on p.account_id = b.account_id " +
                "order by id, p.sequence")
                                           .setParameter("pivot", pivot)
                                           .setParameter("distance", distance)
                                           .setParameter("gender", gender)
                                           .setParameter("minAge", minAge)
                                           .setParameter("maxAge", maxAge)
                                           .setParameter("limit", limit)
                                           .setParameter("offset", offset)
                                           .getResultList();
        return CardDTOResultTransformer.mapList(rows);
    }
}
