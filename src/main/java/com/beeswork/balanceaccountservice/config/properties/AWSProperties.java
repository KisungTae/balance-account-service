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
@ConfigurationProperties("aws")
public class AWSProperties {

    private String balancePhotoBucket;
}
