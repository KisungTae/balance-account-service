package com.beeswork.balanceaccountservice.config.properties;


import com.beeswork.balanceaccountservice.constant.Delimiter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Getter
@Setter
@Component
@NoArgsConstructor
@ConfigurationProperties("aws")
public class AWSProperties {

    private String photoBucket;
    private String photoBucketURL;
    private String photoURLDomain;
    private String region;
}
