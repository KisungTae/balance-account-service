package com.beeswork.balanceaccountservice.dto.s3;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
@Setter
public class PreSignedUrl {
    private String              url;
    private Map<String, String> fields = new LinkedHashMap<>();

    private static final String       HMAC_SHA256          = "AWS4-HMAC-SHA256";
    private static final Duration     EXPIRATION_DURATION  = Duration.of(3L, ChronoUnit.MINUTES);
    private static final String       ACL                  = "public-read";
    private static final List<Object> CONTENT_LENGTH_RANGE = Arrays.asList("content-length-range", 1, 1048576);
//    private static final List<Object> CONTENT_LENGTH_RANGE = Arrays.asList("content-length-range", 1, 10);

    public PreSignedUrl(String url, String accessKeyId, String region, String bucket, String key, Instant date) {
        this.url = url;
        this.fields.put("key", key);
        this.fields.put("bucket", bucket);
        this.fields.put("X-Amz-Algorithm", HMAC_SHA256);
        this.fields.put("X-Amz-Credential", credentialsId(accessKeyId, date, region));
        this.fields.put("X-Amz-Date", asAwsDate(date));
        this.fields.put("acl", ACL);
    }

    private static String asAwsDate(Instant instant) {
        return instant.toString().replaceAll("[:\\-]|\\.\\d{3}", "");
    }

    public static String asAwsShortDate(Instant instant) {
        return asAwsDate(instant).substring(0, 8);
    }

    private static String credentialsId(String accessKeyId, Instant date, String region) {
        return String.format("%s/%s/%s/%s/%s",
                             accessKeyId,
                             asAwsShortDate(date),
                             region,
                             "s3",
                             "aws4_request");
    }

    public UploadPolicy getUploadPolicy(Instant date) {
        UploadPolicy uploadPolicy = new UploadPolicy();
        uploadPolicy.expiration = date.plus(EXPIRATION_DURATION);
        uploadPolicy.conditions.addAll(this.fields.entrySet());
        uploadPolicy.conditions.add(CONTENT_LENGTH_RANGE);
        return uploadPolicy;
    }

    public void sign(String encodedPolicy, String signature) {
        this.fields.put("Policy", encodedPolicy);
        this.fields.put("X-Amz-Signature", signature);
    }

    @Getter
    @Setter
    static public class UploadPolicy {
        private Instant      expiration;
        private List<Object> conditions = new ArrayList<>();
    }
}
