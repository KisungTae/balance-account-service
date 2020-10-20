package com.beeswork.balanceaccountservice.service.swipe;

import java.util.UUID;

public interface SwipeInterService {

    boolean existsByClicked(UUID swiperId, UUID swipedId, boolean clicked);
}
