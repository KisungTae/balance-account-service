package com.beeswork.balanceaccountservice.dao.photo;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.photo.Photo;

import java.util.List;
import java.util.UUID;

public interface PhotoDAO extends BaseDAO<Photo> {
    boolean existsByKey(UUID accountId, String key);
    List<Photo> findAllBy(UUID accountId, int limit);
    long countBy(UUID accountId);
    Photo getProfilePhotoBy(UUID accountId);
}
