package com.beeswork.balanceaccountservice.service.count;

import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.count.CountTabDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CountServiceImpl implements CountService {

    private final SwipeDAO swipeDAO;
    private final MatchDAO matchDAO;

    @Autowired
    public CountServiceImpl(SwipeDAO swipeDAO, MatchDAO matchDAO) {
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
    }

    @Override
    public List<CountTabDTO> countTabs(UUID accountId) {
        return null;
    }
}
