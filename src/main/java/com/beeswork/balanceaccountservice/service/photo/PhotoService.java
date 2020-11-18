package com.beeswork.balanceaccountservice.service.photo;

import com.beeswork.balanceaccountservice.dto.photo.GenerateS3PreSignedURLDTO;

public interface PhotoService {

    void deletePhoto(String accountId, String identityToken, String photoKey);
    void swapPhotoSequence(String accountId, String identityToken, String firstPhotoKey, String secondPhotoKey);
}
