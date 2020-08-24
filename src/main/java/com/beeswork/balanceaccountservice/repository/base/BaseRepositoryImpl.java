package com.beeswork.balanceaccountservice.repository.base;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@AllArgsConstructor
public abstract class BaseRepositoryImpl<E> implements BaseRepository<E> {

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
