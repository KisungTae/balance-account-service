package com.beeswork.balanceaccountservice.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties("connection-pool")
public class ConnectionPoolProperties {

    private int initialPoolSize;
    private int minPoolSize;
    private int maxPoolSize;
    private int maxIdleTime;
}
