package com.beeswork.balanceaccountservice.service.s3;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.photo.PhotoDAO;
import com.beeswork.balanceaccountservice.dto.s3.PreSignedUrl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.photo.PhotoAlreadyExistsException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Service
public class S3ServiceImpl extends BaseServiceImpl implements S3Service {

    private final DefaultAwsRegionProviderChain      defaultAwsRegionProviderChain;
    private final DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain;
    private final ObjectMapper                       objectMapper;
    private final AmazonS3                           amazonS3;
    private final AccountDAO                         accountDAO;
    private final PhotoDAO                           photoDAO;

    private final static String BALANCE_PHOTO_BUCKET = "test-balance-photo-bucket";
    private final static String S3_URL               = "https://s3.%s.amazonaws.com/%s";

    @Autowired
    public S3ServiceImpl(DefaultAwsRegionProviderChain defaultAwsRegionProviderChain,
                         DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain,
                         ObjectMapper objectMapper,
                         AmazonS3 amazonS3,
                         AccountDAO accountDAO, PhotoDAO photoDAO) {
        this.defaultAwsRegionProviderChain = defaultAwsRegionProviderChain;
        this.defaultAWSCredentialsProviderChain = defaultAWSCredentialsProviderChain;
        this.objectMapper = objectMapper;
        this.amazonS3 = amazonS3;
        this.accountDAO = accountDAO;
        this.photoDAO = photoDAO;
    }

    @Override
    @Async("processExecutor")
    public void deletePhoto(UUID accountId, String photoKey) {
        String key = generateKey(accountId.toString(), photoKey);
        amazonS3.deleteObject(new DeleteObjectRequest(BALANCE_PHOTO_BUCKET, key));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PreSignedUrl generatePreSignedUrl(UUID accountId, String photoKey) throws JsonProcessingException {
        if (photoDAO.existsByKey(accountId, photoKey)) throw new PhotoAlreadyExistsException();

        String key = generateKey(accountId.toString(), photoKey);
        String region = defaultAwsRegionProviderChain.getRegion();
        String accessKeyId = defaultAWSCredentialsProviderChain.getCredentials().getAWSAccessKeyId();
        String secretKey = defaultAWSCredentialsProviderChain.getCredentials().getAWSSecretKey();
        String endpoint = String.format(S3_URL, region, BALANCE_PHOTO_BUCKET);

        Instant now = Instant.now();
        PreSignedUrl preSignedUrl = new PreSignedUrl(endpoint, accessKeyId, region, BALANCE_PHOTO_BUCKET, key, now);
        String encodePolicy = encodePolicy(preSignedUrl.getUploadPolicy(now));
        String signature = computeSignature(encodePolicy, secretKey, region, now);
        preSignedUrl.sign(encodePolicy, signature);
        return preSignedUrl;
    }

    @Override
    @Async("processExecutor")
    public void deletePhotosAsync(UUID accountId, List<String> photoKeys) {
        ArrayList<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();
        for (String photoKey : photoKeys)
            keys.add(new DeleteObjectsRequest.KeyVersion(generateKey(accountId.toString(), photoKey)));

        if (!keys.isEmpty()) {
            DeleteObjectsRequest request = new DeleteObjectsRequest(BALANCE_PHOTO_BUCKET).withKeys(keys).withQuiet(true);
            amazonS3.deleteObjects(request);
        }
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

    private static String generateKey(String accountId, String photoKey) {
        return accountId + "/" + photoKey;
    }
}
