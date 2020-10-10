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
@ConfigurationProperties("firebase")
public class FirebaseProperties {

    private String serviceAccountKeyPath;
    private String databaseURL;
}
