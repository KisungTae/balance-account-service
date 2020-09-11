package com.beeswork.balanceaccountservice.config;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomPostgreSQLDialect extends PostgreSQL82Dialect {

    public CustomPostgreSQLDialect() {
        super();
//        registerFunction("ST_WITHIN", new StandardSQLFunction("ST_WITHIN", StandardBasicTypes.));
    }
}
