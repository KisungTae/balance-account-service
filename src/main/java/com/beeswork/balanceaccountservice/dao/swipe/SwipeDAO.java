package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;

import java.util.List;
import java.util.UUID;

public interface SwipeDAO extends BaseDAO<Swipe> {

    Swipe findBySwiperIdAndSwipedId(UUID swiperId, UUID swipedId, boolean writeLock);
    List<SwipeDTO> findClicks(UUID swipedId, int startPosition, int loadSize);
    List<SwipeDTO> findClicks(UUID swipedId, UUID lastSwiperId, int loadSize);
    long countClicks(UUID swipedId);
}
