package com.beeswork.balanceaccountservice.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class DatabaseConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public GeometryFactory geometryFactory() { return new GeometryFactory(); }

}
