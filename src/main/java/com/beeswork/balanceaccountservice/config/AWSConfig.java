package com.beeswork.balanceaccountservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AWSConfig {

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                                    .withRegion(Regions.AP_NORTHEAST_2)
                                    .build();
    }

    @Bean
    public DefaultAwsRegionProviderChain defaultAwsRegionProviderChain() {
        return new DefaultAwsRegionProviderChain();
    }

    @Bean
    public DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain() {
        return new DefaultAWSCredentialsProviderChain();
    }
}
