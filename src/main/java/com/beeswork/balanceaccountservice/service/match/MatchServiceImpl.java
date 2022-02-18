package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.match.UnmatchAuditDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.UnmatchAudit;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {
    private final AccountDAO         accountDAO;
    private final MatchDAO           matchDAO;
    private final UnmatchAuditDAO    unmatchAuditDAO;
    private final ReportService reportService;

    @Autowired
    public MatchServiceImpl(AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            UnmatchAuditDAO unmatchAuditDAO,
                            ReportService reportService) {
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.unmatchAuditDAO = unmatchAuditDAO;
        this.reportService = reportService;
    }


    @Override
    public List<MatchDTO> fetchMatches(UUID swiperId, UUID lastSwipedId, int loadSize, MatchPageFilter matchPageFilter) {

        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListMatchesDTO listMatches(UUID accountId, Date fetchedAt) {
        return null;
//        ListMatchesDTO listMatchesDTO = new ListMatchesDTO(fetchedAt);
//        List<MatchDTO> matchDTOs = matchDAO.findAllAfter(accountId, offsetFetchedAt(fetchedAt));
//
//        if (matchDTOs != null) {
//            for (MatchDTO matchDTO : matchDTOs) {
//                if (matchDTO.getUnmatched() || matchDTO.getDeleted()) {
//                    matchDTO.setSwipedProfilePhotoKey(null);
//                    matchDTO.setCreatedAt(null);
//                    matchDTO.setActive(true);
//                    matchDTO.setUnmatched(true);
//                }
//                if (matchDTO.getUpdatedAt().after(listMatchesDTO.getFetchedAt())) {
//                    listMatchesDTO.setFetchedAt(matchDTO.getUpdatedAt());
//                }
//
//                matchDTO.setUpdatedAt(null);
//                matchDTO.setDeleted(null);
//            }
//        }
//        listMatchesDTO.setMatchDTOs(matchDTOs);
//        return listMatchesDTO;
    }

    @Override
    @Transactional
    public void unmatch(UUID swiperId, UUID swipedId) {
        Date now = new Date();
        unmatch(swiperId, swipedId, now);
        if (!unmatchAuditDAO.existsBy(swiperId, swipedId)) {
            Account swiper = accountDAO.findById(swiperId, false);
            Account swiped = accountDAO.findById(swipedId, false);
            if (swiped == null) {
                throw new SwipedNotFoundException();
            }
            UnmatchAudit unmatchAudit = new UnmatchAudit(swiper, swiped, now);
            unmatchAuditDAO.persist(unmatchAudit);
        }
    }

    @Override
    @Transactional
    public void reportMatch(UUID reporterId, UUID reportedId, int reportReasonId, String description) {
        Date now = new Date();
        unmatch(reporterId, reportedId, now);
        reportService.createReport(reporterId, reportedId, reportReasonId, description, now);
    }

    @SuppressWarnings("Duplicates")
    private void unmatch(UUID swiperId, UUID swipedId, Date now) {
        // NOTE 1. if you fetch an entity with writeLock and fetch the same entity without write lock,
        //         you still need to wait for the write lock to be released when you try to write on the second entity
        Match swiperMatch, swipedMatch;
        if (swiperId.compareTo(swipedId) > 0) {
            swiperMatch = matchDAO.findBy(swiperId, swipedId, true);
            swipedMatch = matchDAO.findBy(swipedId, swiperId, true);
        } else {
            swipedMatch = matchDAO.findBy(swipedId, swiperId, true);
            swiperMatch = matchDAO.findBy(swiperId, swipedId, true);
        }

        if (swiperMatch == null || swipedMatch == null) {
            throw new MatchNotFoundException();
        }

        if (swiperMatch.isUnmatched() && swipedMatch.isUnmatched()) {
            if (!swiperMatch.isDeleted()) {
                swiperMatch.setDeleted(true);
                swiperMatch.setUpdatedAt(now);
            }
        } else {
            swiperMatch.setUnmatched(true);
            swiperMatch.setDeleted(true);
            swiperMatch.setUpdatedAt(now);
            swipedMatch.setUnmatched(true);
            swipedMatch.setUpdatedAt(now);
        }
    }
}

