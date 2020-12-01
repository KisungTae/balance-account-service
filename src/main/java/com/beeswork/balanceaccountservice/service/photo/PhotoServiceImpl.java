package com.beeswork.balanceaccountservice.service.photo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.account.PhotoDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.exception.photo.PhotoInvalidDeleteException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PhotoServiceImpl extends BaseServiceImpl implements PhotoService {

    private final AccountDAO accountDAO;
    private final AmazonS3 amazonS3;

    private final String BUCKET = "balance-photo-bucket";

    @Autowired
    public PhotoServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, AmazonS3 amazonS3) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.amazonS3 = amazonS3;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<PhotoDTO> listPhotos(String accountId, String identityToken) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return modelMapper.map(account.getPhotos(), new TypeToken<List<PhotoDTO>>() {}.getType());
    }

    @Override
    @Transactional
    public void deletePhoto(String accountId, String identityToken, String photoKey) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        if (account.getPhotos().size() <= 1)
            throw new PhotoInvalidDeleteException();

        Photo photo = account.getPhotos()
                             .stream()
                             .filter(p -> p.getKey().equals(photoKey))
                             .findAny()
                             .orElseThrow(PhotoNotFoundException::new);

        String key = account.getId().toString() + "/" + photo.getKey();
        amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, key));
        account.getPhotos().remove(photo);
        resetRepPhoto(account);
    }

    private void resetRepPhoto(Account account) {

        Collections.sort(account.getPhotos());
        Photo repPhoto = account.getPhotos().get(0);

        if (!account.getRepPhotoKey().equals(repPhoto.getKey())) {
            account.setRepPhotoKey(repPhoto.getKey());
            account.setUpdatedAt(new Date());
        }
    }

    @Override
    @Transactional
    public void reorderPhotos(String accountId, String identityToken, Map<String, Long> photoOrders) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        for (Photo photo : account.getPhotos()) {
            if (photoOrders.containsKey(photo.getKey()))
                photo.setSequence(photoOrders.get(photo.getKey()));
        }
        resetRepPhoto(account);
    }
}
