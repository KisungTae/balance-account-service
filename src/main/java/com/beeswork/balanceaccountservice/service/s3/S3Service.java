package com.beeswork.balanceaccountservice.service.s3;

import com.beeswork.balanceaccountservice.dto.s3.PreSignedUrl;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.UUID;

public interface S3Service {
    void deletePhoto(UUID accountId, String photoKey);
    PreSignedUrl generatePreSignedUrl(UUID accountId, String photoKey) throws JsonProcessingException;
    void deletePhotosAsync(UUID accountId, List<String> photoKeys);
}
