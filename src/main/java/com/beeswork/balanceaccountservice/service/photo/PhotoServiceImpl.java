package com.beeswork.balanceaccountservice.service.photo;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.photo.PhotoDAO;
import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
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
    private final ModelMapper modelMapper;
    private final PhotoDAO photoDAO;

    private static final int MAX_NUM_OF_PHOTOS = 6;

    @Autowired
    public PhotoServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO, PhotoDAO photoDAO) {
        this.accountDAO = accountDAO;
        this.modelMapper = modelMapper;
        this.photoDAO = photoDAO;
    }

    @Override
    @Transactional
    public void savePhoto(UUID accountId, UUID identityToken, String photoKey, int sequence) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        List<Photo> photos = account.getPhotos();
        if (photos.size() >= MAX_NUM_OF_PHOTOS) throw new PhotoNumReachedMaxException();

        Photo photo = photos.stream()
                            .filter(p -> p.getPhotoId().getKey().equals(photoKey))
                            .findAny()
                            .orElse(null);

        if (photo == null) photos.add(new Photo(account, photoKey, sequence, new Date()));
        else photo.setSequence(sequence);
        resetProfilePhoto(account, photos);
        accountDAO.persist(account);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<PhotoDTO> listPhotos(UUID accountId, UUID identityToken) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        return modelMapper.map(account.getPhotos(), new TypeToken<List<PhotoDTO>>() {}.getType());
    }

    @Override
    @Transactional
    public void deletePhoto(UUID accountId, UUID identityToken, String photoKey) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        List<Photo> photos = account.getPhotos();

        Photo photo = photos.stream()
                            .filter(p -> p.getPhotoId().getKey().equals(photoKey))
                            .findAny()
                            .orElseThrow(PhotoNotFoundException::new);

        if (photos.size() <= 1) throw new PhotoInvalidDeleteException();
        photos.remove(photo);
        resetProfilePhoto(account, photos);
    }

    private void resetProfilePhoto(Account account, List<Photo> photos) {
        if (photos.size() <= 0) return;
        Collections.sort(photos);
        Photo profilePhoto = photos.get(0);

        if (!profilePhoto.getPhotoId().getKey().equals(account.getProfilePhotoKey())) {
            account.setProfilePhotoKey(profilePhoto.getPhotoId().getKey());
            account.setUpdatedAt(new Date());
        }
    }

    @Override
    @Transactional
    public void reorderPhotos(UUID accountId, UUID identityToken, Map<String, Integer> photoOrders) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        List<Photo> photos = account.getPhotos();
        Date updatedAt = new Date();
        for (Photo photo : photos) {
            if (photoOrders.containsKey(photo.getPhotoId().getKey())) {
                photo.setSequence(photoOrders.get(photo.getPhotoId().getKey()));
                photo.setUpdatedAt(updatedAt);
            }
        }
        resetProfilePhoto(account, photos);
    }
}
