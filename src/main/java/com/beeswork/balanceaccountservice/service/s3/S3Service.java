package com.beeswork.balanceaccountservice.service.s3;

import com.beeswork.balanceaccountservice.dto.s3.PreSignedUrl;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface S3Service {
    void deletePhoto(String accountId, String photoKey);
    PreSignedUrl preSignedUrl(String accountId, String photoKey) throws JsonProcessingException;
}
