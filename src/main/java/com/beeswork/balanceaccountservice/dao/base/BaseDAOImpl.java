package com.beeswork.balanceaccountservice.dao.base;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;

import javax.persistence.EntityManager;

@AllArgsConstructor
public abstract class BaseDAOImpl<E> implements BaseDAO<E> {

    protected EntityManager entityManager;
    protected JPAQueryFactory jpaQueryFactory;

    @Override
    public void persist(E entity) {
        entityManager.persist(entity);
    }

    @Override
    public void remove(E entity) {
        entityManager.remove(entity);
    }

    @Override
    public void flush() {
        entityManager.flush();
    }


}
