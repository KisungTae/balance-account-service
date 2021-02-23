package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SwipeDAO extends BaseDAO<Swipe> {

    Swipe findById(SwipeId swipeId);
    Swipe findWithAccounts(UUID swiperId, UUID swipedId);
    boolean existsByClicked(UUID swiperId, UUID swipedId, boolean clicked);
    List<ClickedProjection> findAllClickedAfter(UUID swipedId, Date fetchedAt);
    List<ClickProjection> findAllClickAfter(UUID swiperId, Date fetchedAt);
    Swipe findBy(UUID swiperId, UUID swipedId);
}
