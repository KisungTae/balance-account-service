package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SwipeDAO extends BaseDAO<Swipe> {

    Swipe findById(SwipeId swipeId);
    Swipe findByIdWithLock(SwipeId swipeId);
    List<SwipeDTO> findSwipes(UUID accountId, Date fetchedAt);
    List<SwipeDTO> findClicks(UUID accountId, Date fetchedAt);
}
