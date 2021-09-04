package com.beeswork.balanceaccountservice.config.properties;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
@ConfigurationProperties("stomp")
public class StompProperties {
    private String destinationPrefix;
    private String stompBrokerRelay;
    private String relayHost;
    private int relayPort;
    private int maxMessageSize;
    private String clientLogin;
    private String clientPasscode;
    private String endPoint;
}
