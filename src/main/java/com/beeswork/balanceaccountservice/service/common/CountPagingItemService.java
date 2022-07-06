package com.beeswork.balanceaccountservice.service.common;

import com.beeswork.balanceaccountservice.dto.common.CountPagingItemDTO;

import java.util.UUID;

public interface CountPagingItemService {

    CountPagingItemDTO countSwipes(UUID swipedId);
    CountPagingItemDTO countMatches(UUID swiperId);
    CountPagingItemDTO countUnreadChats(UUID swiperId);
}
