package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

import java.util.UUID;

public interface SwipeDAO extends BaseDAO<Swipe> {

    boolean balancedExists(UUID swiperId, UUID swipedId);
    Swipe findByIdWithAccounts(Long swipeId, UUID swiperId, UUID swipedId) throws SwipeNotFoundException;
    boolean existsByAccountIdsAndBalanced(UUID swiperId, UUID swipedId, boolean balanced);
}
