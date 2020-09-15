package com.beeswork.balanceaccountservice.dao.swipe;

import java.util.UUID;

public interface SwipeDAO {

    boolean balancedExists(UUID swiperId, UUID swipedId);
}
