package com.beeswork.balanceaccountservice.service.s3;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.beeswork.balanceaccountservice.dto.s3.PreSignedUrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;


@Service
public class S3ServiceImpl implements S3Service {

    private final DefaultAwsRegionProviderChain      defaultAwsRegionProviderChain;
    private final DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain;
    private final ObjectMapper                       objectMapper;
    private final AmazonS3                           amazonS3;

    private final static String BALANCE_PHOTO_BUCKET = "balance-photo-bucket";
    private final static String S3_URL               = "https://s3.%s.amazonaws.com/%s";

    @Autowired
    public S3ServiceImpl(DefaultAwsRegionProviderChain defaultAwsRegionProviderChain,
                         DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain,
                         ObjectMapper objectMapper, AmazonS3 amazonS3) {
        this.defaultAwsRegionProviderChain = defaultAwsRegionProviderChain;
        this.defaultAWSCredentialsProviderChain = defaultAWSCredentialsProviderChain;
        this.objectMapper = objectMapper;
        this.amazonS3 = amazonS3;
    }

    @Override
    @Async("processExecutor")
    public void deletePhoto(String accountId, String photoKey) {
        String key = accountId + "/" + photoKey;
        amazonS3.deleteObject(new DeleteObjectRequest(BALANCE_PHOTO_BUCKET, key));
    }

    @Override
    public PreSignedUrl preSignedUrl(String photoKey) throws JsonProcessingException {
        String region = defaultAwsRegionProviderChain.getRegion();
        String accessKeyId = defaultAWSCredentialsProviderChain.getCredentials().getAWSAccessKeyId();
        String secretKey = defaultAWSCredentialsProviderChain.getCredentials().getAWSSecretKey();
        String endpoint = String.format(S3_URL, region, BALANCE_PHOTO_BUCKET);

        Instant now = Instant.now();
        PreSignedUrl preSignedUrl = new PreSignedUrl(endpoint, accessKeyId, region, BALANCE_PHOTO_BUCKET, photoKey,
                                                     now);
        String encodePolicy = encodePolicy(preSignedUrl.getUploadPolicy(now));
        String signature = computeSignature(encodePolicy, secretKey, region, now);
        preSignedUrl.sign(encodePolicy, signature);
        return preSignedUrl;
    }

    private String computeSignature(String encodedPolicy, String secretKey, String region, Instant date) {
        String shortDate = PreSignedUrl.asAwsShortDate(date);
        byte[] dateKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, "AWS4" + secretKey).hmac(shortDate);
        byte[] dateRegionKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, dateKey).hmac(region);
        byte[] dateRegionServiceKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, dateRegionKey).hmac("s3");
        byte[] signingKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, dateRegionServiceKey).hmac("aws4_request");
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, signingKey).hmacHex(encodedPolicy);
    }

    private String encodePolicy(PreSignedUrl.UploadPolicy uploadPolicy) throws JsonProcessingException {
        String policyJson = objectMapper.writeValueAsString(uploadPolicy);
        return Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
    }
}
