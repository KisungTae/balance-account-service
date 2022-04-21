package com.beeswork.balanceaccountservice.config.properties;


import com.beeswork.balanceaccountservice.constant.Delimiter;
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
    private String s3Url;
    private String accessKeyId;
    private String secretKey;
    private String region;

    public String getBalancePhotoBucketURL() {
        return String.format(s3Url, region, balancePhotoBucket);
    }
}
