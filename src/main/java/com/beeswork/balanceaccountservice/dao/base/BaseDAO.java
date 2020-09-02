package com.beeswork.balanceaccountservice.dao.base;

public interface BaseDAO<E> {
    void flush();
    void persist(E entity);
    void remove(E entity);
}
