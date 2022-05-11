package com.beeswork.balanceaccountservice.dao.profile;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.profile.Card;
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
    public Profile findById(UUID accountId, boolean writeLock) {
        return entityManager.find(Profile.class, accountId, writeLock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Card findCard(UUID swipedId, Point pivot) {
        List<Card> cards = entityManager.createNativeQuery(
                "select cast(pr.account_id as varchar), pr.name, pr.birth_year, pr.gender, pr.height, pr.about, st_distance(pr.location, :pivot), ph.key " +
                "from profile pr " +
                "left join photo ph on pr.account_id = ph.account_id " +
                "where pr.account_id = :swipedId " +
                "order by ph.sequence", "Card")
                                        .setParameter("pivot", pivot)
                                        .setParameter("swipedId", swipedId)
                                        .getResultList();
        combineCards(cards);
        return cards.isEmpty() ? null : cards.get(0);
    }

    @Override
    public boolean existsById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qProfile).where(qProfile.accountId.eq(accountId)).fetchCount() > 0;
    }


    //  NOTE 1. If you want to use st_dwithin in HQL, then you can do where dwithin(pr.location, :pivot, :distance) = TRUE
    @Override
    @SuppressWarnings("unchecked")
    public List<Card> findCards(UUID accountId,
                                int distance,
                                int minAge,
                                int maxAge,
                                boolean gender,
                                int limit,
                                int offset,
                                Point pivot) {
        List<Card> cards = entityManager.createNativeQuery(
                "select cast(b.account_id as varchar) as account_id, b.name, b.gender, b.birth_year, b.height, b.about, st_distance(b.location, :pivot) as distance, p.key as photo_key " +
                "from (select * " +
                "      from profile " +
                "      left join swipe on profile.account_id = swipe.swiper_id  " +
                "      where st_dwithin(location, :pivot, :distance) " +
//                "        and account_id != :accountId " +
                "        and gender = :gender " +
                "        and birth_year <= :minAge " +
                "        and birth_year >= :maxAge " +
                "        and enabled = true " +
                "        and clicked = false" +
                "        and matched = false " +
                "        order by score " +
                "       limit :limit " +
                "       offset :offset) as b " +
                "left join photo as p " +
                "on p.account_id = b.account_id " +
                "order by account_id, p.sequence", "Card")
                                        .setParameter("pivot", pivot)
                                        .setParameter("distance", distance)
//                                        .setParameter("accountId", accountId)
                                        .setParameter("gender", gender)
                                        .setParameter("minAge", minAge)
                                        .setParameter("maxAge", maxAge)
                                        .setParameter("limit", limit)
                                        .setParameter("offset", offset)
                                        .getResultList();

        combineCards(cards);
        return cards;
    }

    private void combineCards(List<Card> cards) {
        if (cards == null) {
            return;
        }
        Card card = null;
        int index = 0;
        while (index < cards.size()) {
            Card currentCard = cards.get(index);
            if (card == null || !card.getAccountId().equals(currentCard.getAccountId())) {
                currentCard.getPhotoKeys().add(currentCard.getPhotoKey());
                currentCard.setPhotoKey(null);
                card = currentCard;
                index++;
            } else {
                card.getPhotoKeys().add(currentCard.getPhotoKey());
                cards.remove(index);
            }
        }
    }
}
