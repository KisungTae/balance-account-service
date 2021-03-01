package com.beeswork.balanceaccountservice.service.photo;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.exception.photo.PhotoInvalidDeleteException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNotFoundException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNumReachedMaxException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import net.sf.ehcache.CacheManager;
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
    public PhotoServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional
    public void addPhoto(UUID accountId, UUID identityToken, String photoKey, int sequence) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        List<Photo> photos = account.getPhotos();
        if (photos.size() >= MAX_NUM_OF_PHOTOS)
            throw new PhotoNumReachedMaxException();

        Photo photo = photos.stream()
                            .filter(p -> p.getPhotoId().getKey().equals(photoKey))
                            .findAny()
                            .orElse(null);

        if (photo == null) photos.add(new Photo(account, photoKey, sequence));
        else photo.setSequence(sequence);
        resetRepPhoto(account, photos);
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
        resetRepPhoto(account, photos);
    }

    private void resetRepPhoto(Account account, List<Photo> photos) {
        if (photos.size() <= 0) return;
        Collections.sort(photos);
        Photo repPhoto = photos.get(0);

        if (!repPhoto.getPhotoId().getKey().equals(account.getRepPhotoKey())) {
            account.setRepPhotoKey(repPhoto.getPhotoId().getKey());
            account.setUpdatedAt(new Date());
        }
    }

    @Override
    @Transactional
    public void reorderPhotos(UUID accountId, UUID identityToken, Map<String, Integer> photoOrders) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        List<Photo> photos = account.getPhotos();
        for (Photo photo : photos) {
            if (photoOrders.containsKey(photo.getPhotoId().getKey()))
                photo.setSequence(photoOrders.get(photo.getPhotoId().getKey()));
        }
        resetRepPhoto(account, photos);
    }
}
