package com.beeswork.balanceaccountservice.dao.photo;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.photo.QPhoto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class PhotoDAOImpl extends BaseDAOImpl<Photo> implements PhotoDAO {

    private final QPhoto qPhoto = QPhoto.photo;

    @Autowired
    public PhotoDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Photo findFirstBySequence(UUID accountId) {
        return jpaQueryFactory.selectFrom(qPhoto)
                              .where(qPhoto.accountId.eq(accountId))
                              .orderBy(qPhoto.sequence.asc())
                              .limit(1).fetchFirst();
    }
}
