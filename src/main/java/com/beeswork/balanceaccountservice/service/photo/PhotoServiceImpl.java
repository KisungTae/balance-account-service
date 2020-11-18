package com.beeswork.balanceaccountservice.service.photo;

import com.amazonaws.services.s3.AmazonS3;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.photo.PhotoDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PhotoServiceImpl extends BaseServiceImpl implements PhotoService {

    private final PhotoDAO photoDAO;
    private final AccountDAO accountDAO;
    private final AmazonS3 amazonS3;

    private final String BUCKET = "balance-photo-bucket";

    @Autowired
    public PhotoServiceImpl(ModelMapper modelMapper, PhotoDAO photoDAO, AccountDAO accountDAO, AmazonS3 amazonS3) {
        super(modelMapper);
        this.photoDAO = photoDAO;
        this.accountDAO = accountDAO;
        this.amazonS3 = amazonS3;
    }


    @Override
    public void deletePhoto(String accountId, String identityToken, String photoKey) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        

    }

    @Override
    public void swapPhotoSequence(String accountId, String identityToken, String firstPhotoKey, String secondPhotoKey) {

    }
}
