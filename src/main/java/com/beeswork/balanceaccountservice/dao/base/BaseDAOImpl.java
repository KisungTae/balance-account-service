package com.beeswork.balanceaccountservice.dao.base;


import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.BaseException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.sql.Connection;

@AllArgsConstructor
public abstract class BaseDAOImpl<E> implements BaseDAO<E> {

    protected EntityManager   entityManager;
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
