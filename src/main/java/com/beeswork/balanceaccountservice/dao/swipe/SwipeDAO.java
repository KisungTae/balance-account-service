package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;

import java.util.List;
import java.util.UUID;

public interface SwipeDAO extends BaseDAO<Swipe> {

    Swipe findBy(UUID swiperId, UUID swipedId, boolean writeLock);
    List<SwipeDTO> findAllBy(UUID swipedId, int startPosition, int loadSize);
    List<SwipeDTO> findAllBy(UUID swipedId, Long lastSwipeId, int loadSize);
    List<SwipeDTO> findAllBy(UUID swipedId, Long loadKey, int loadSize, boolean isAppend, boolean isIncludeLoadKey);
    long countSwipesBy(UUID swipedId);
}
