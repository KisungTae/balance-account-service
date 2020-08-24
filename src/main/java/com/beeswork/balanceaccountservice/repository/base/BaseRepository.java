package com.beeswork.balanceaccountservice.repository.base;

public interface BaseRepository<E> {
    void flush();
    void persist(E entity);
    void remove(E entity);
}
