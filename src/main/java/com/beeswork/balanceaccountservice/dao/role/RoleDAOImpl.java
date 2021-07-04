package com.beeswork.balanceaccountservice.dao.role;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.login.QRole;
import com.beeswork.balanceaccountservice.entity.login.Role;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class RoleDAOImpl extends BaseDAOImpl<Role> implements RoleDAO {

    private final QRole qRole = QRole.role;

    @Autowired
    public RoleDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }



}
