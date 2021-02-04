package com.beeswork.balanceaccountservice.service.photo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.account.PhotoDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.exception.photo.PhotoInvalidDeleteException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNotFoundException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNumReachedMaxException;
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

    private static final int MAX_NUM_OF_PHOTOS = 6;

    @Autowired
    public PhotoServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional
    public void addPhoto(UUID accountId, UUID identityToken, String photoKey, int sequence) {
        Account account = accountDAO.findWithPhotos(accountId, identityToken);
        checkIfAccountValid(account);

        if (account.getPhotos().size() >= MAX_NUM_OF_PHOTOS)
            throw new PhotoNumReachedMaxException();

        Photo photo = account.getPhotos()
                             .stream()
                             .filter(p -> p.getKey().equals(photoKey))
                             .findAny()
                             .orElse(null);

        if (photo == null) account.getPhotos().add(new Photo(account, photoKey, sequence));
        else photo.setSequence(sequence);
        resetRepPhoto(account);
        accountDAO.persist(account);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<PhotoDTO> listPhotos(UUID accountId, UUID identityToken) {
        Account account = accountDAO.findWithPhotos(accountId, identityToken);
        checkIfAccountValid(account);
        return modelMapper.map(account.getPhotos(), new TypeToken<List<PhotoDTO>>() {}.getType());
    }

    @Override
    @Transactional
    public void deletePhoto(UUID accountId, UUID identityToken, String photoKey) {
        Account account = accountDAO.findWithPhotos(accountId, identityToken);
        checkIfAccountValid(account);

        Photo photo = account.getPhotos()
                             .stream()
                             .filter(p -> p.getKey().equals(photoKey))
                             .findAny()
                             .orElseThrow(PhotoNotFoundException::new);

        if (account.getPhotos().size() <= 1)
            throw new PhotoInvalidDeleteException();

        account.getPhotos().remove(photo);
        resetRepPhoto(account);
    }

    private void resetRepPhoto(Account account) {
        if (account.getPhotos().size() <= 0) return;

        Collections.sort(account.getPhotos());
        Photo repPhoto = account.getPhotos().get(0);

        if (repPhoto != null && !repPhoto.getPhotoId().getKey().equals(account.getRepPhotoKey())) {
            account.setRepPhotoKey(repPhoto.getPhotoId().getKey());
            account.setRepPhotoUpdatedAt(new Date());
        }
    }

    @Override
    @Transactional
    public void reorderPhotos(UUID accountId, UUID identityToken, Map<String, Integer> photoOrders) {
        Account account = accountDAO.findWithPhotos(accountId, identityToken);
        checkIfAccountValid(account);

        for (Photo photo : account.getPhotos()) {
            if (photoOrders.containsKey(photo.getKey()))
                photo.setSequence(photoOrders.get(photo.getKey()));
        }
        resetRepPhoto(account);
    }
}
