package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SwipeDAO extends BaseDAO<Swipe> {

    boolean clickedExists(UUID swiperId, UUID swipedId);

    Swipe findByIdWithAccounts(Long swipeId, UUID swiperId, UUID swipedId) throws SwipeNotFoundException;

    boolean existsByAccountIdsAndClicked(UUID swiperId, UUID swipedId, boolean clicked);

    List<ClickedProjection> findAllClickedAfter(UUID swipedId, Date fetchedAt);
}
