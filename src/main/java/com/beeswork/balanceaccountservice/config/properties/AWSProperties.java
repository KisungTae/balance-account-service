package com.beeswork.balanceaccountservice.config.properties;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties("aws")
public class AWSProperties {

    private String accessKey;
    private String secretKey;
}
