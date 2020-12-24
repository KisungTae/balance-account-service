package com.beeswork.balanceaccountservice.service.photo;

import com.beeswork.balanceaccountservice.dto.account.PhotoDTO;
import com.beeswork.balanceaccountservice.dto.photo.GenerateS3PreSignedURLDTO;

import java.util.List;
import java.util.Map;

public interface PhotoService {

    List<PhotoDTO> listPhotos(String accountId, String identityToken);
    void deletePhoto(String accountId, String identityToken, String photoKey);
    void reorderPhotos(String accountId, String identityToken, Map<String, Integer> photoOrders);
}
