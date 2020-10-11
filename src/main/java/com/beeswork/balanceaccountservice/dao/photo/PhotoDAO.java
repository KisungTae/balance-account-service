package com.beeswork.balanceaccountservice.dao.photo;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.photo.Photo;

import java.util.UUID;

public interface PhotoDAO extends BaseDAO<Photo> {

    Photo findFirstBySequence(UUID accountId);
}
