package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class SwipeMetaDAOImpl extends BaseDAOImpl<SwipeMeta> implements SwipeMetaDAO {

    public SwipeMetaDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

}
