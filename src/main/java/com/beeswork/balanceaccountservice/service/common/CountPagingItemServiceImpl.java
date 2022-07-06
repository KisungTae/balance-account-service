package com.beeswork.balanceaccountservice.service.common;

import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.common.CountPagingItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class CountPagingItemServiceImpl implements CountPagingItemService {

    private final SwipeDAO swipeDAO;
    private final MatchDAO matchDAO;

    @Autowired
    public CountPagingItemServiceImpl(SwipeDAO swipeDAO, MatchDAO matchDAO) {
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CountPagingItemDTO countSwipes(UUID swipedId) {
        return new CountPagingItemDTO(swipeDAO.countSwipes(swipedId), new Date());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CountPagingItemDTO countMatches(UUID swiperId) {
        return new CountPagingItemDTO(matchDAO.countMatches(swiperId), new Date());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CountPagingItemDTO countUnreadChats(UUID swiperId) {
        return new CountPagingItemDTO(matchDAO.countUnreadChats(swiperId), new Date());
    }
}
