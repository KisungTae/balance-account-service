package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.match.UnmatchAuditDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.match.*;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.UnmatchAudit;
import com.beeswork.balanceaccountservice.exception.InternalServerException;
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
import java.util.concurrent.*;


@Service
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {
    private final AccountDAO      accountDAO;
    private final MatchDAO        matchDAO;
    private final UnmatchAuditDAO unmatchAuditDAO;
    private final SwipeDAO        swipeDAO;
    private final ReportService   reportService;

    @Autowired
    public MatchServiceImpl(AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            UnmatchAuditDAO unmatchAuditDAO,
                            SwipeDAO swipeDAO,
                            ReportService reportService) {
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.unmatchAuditDAO = unmatchAuditDAO;
        this.swipeDAO = swipeDAO;
        this.reportService = reportService;
    }

    @Override
    public ListMatchesDTO fetchMatches(final UUID swiperId, final Long lastMatchId, final int loadSize, final MatchPageFilter matchPageFilter) {
        return getListMatchesDTO(() -> doFetchMatches(swiperId, lastMatchId, loadSize, matchPageFilter), swiperId);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<MatchDTO> doFetchMatches(UUID swiperId, Long lastMatchId, int loadSize, MatchPageFilter matchPageFilter) {
        List<MatchDTO> matchDTOs = matchDAO.findAllBy(swiperId, lastMatchId, loadSize, matchPageFilter);
        nullifyMatches(matchDTOs);
        return matchDTOs;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListMatchesDTO listMatches(final UUID swiperId, final int startPosition, final int loadSize, final MatchPageFilter matchPageFilter) {
        return getListMatchesDTO(() -> doListMatches(swiperId, startPosition, loadSize, matchPageFilter), swiperId);
    }

    private List<MatchDTO> doListMatches(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter) {
        List<MatchDTO> matchDTOs = matchDAO.findAllBy(swiperId, startPosition, loadSize, matchPageFilter);
        nullifyMatches(matchDTOs);
        return matchDTOs;
    }

    private void nullifyMatches(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {
            if (matchDTO.isUnmatched() || matchDTO.isSwipedDeleted()) {
                matchDTO.setSwipedName(null);
                matchDTO.setSwipedProfilePhotoKey(null);
            }
        }
    }

    private ListMatchesDTO getListMatchesDTO(final Callable<List<MatchDTO>> listMatchesCallable, final UUID swiperId) {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            Future<List<MatchDTO>> listMatchesFuture = executorService.submit(listMatchesCallable);
            Future<CountMatchesDTO> countMatchesFuture = executorService.submit(() -> countMatches(swiperId));

            ListMatchesDTO listMatchesDTO = new ListMatchesDTO();
            List<MatchDTO> matchDTOs = listMatchesFuture.get(1, TimeUnit.MINUTES);
            CountMatchesDTO countMatchesDTO = countMatchesFuture.get(1, TimeUnit.MINUTES);

            listMatchesDTO.setMatchDTOs(matchDTOs);
            listMatchesDTO.setMatchCount(countMatchesDTO.getCount());
            listMatchesDTO.setMatchCountCountedAt(countMatchesDTO.getCountedAt());

            return listMatchesDTO;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new InternalServerException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CountMatchesDTO countMatches(UUID swiperId) {
        Date now = new Date();
        return new CountMatchesDTO(matchDAO.countMatchesBy(swiperId), now);
    }

    @Override
    @Transactional
    public void syncMatch(UUID swiperId, UUID chatId, long lastReadReceivedChatMessageId) {
        List<Match> matches = matchDAO.findAllBy(chatId, true);
        for (Match match : matches) {
            if (match.getSwiperId().equals(swiperId)) {
                if (match.getLastReadReceivedChatMessageId() < lastReadReceivedChatMessageId) {
                    match.setLastReadReceivedChatMessageId(lastReadReceivedChatMessageId);
                }
            } else {
                if (match.getLastReadByChatMessageId() < lastReadReceivedChatMessageId) {
                    match.setLastReadByChatMessageId(lastReadReceivedChatMessageId);
                }
            }
        }
    }

    @Override
    @Transactional
    public UnmatchDTO unmatch(UUID swiperId, UUID swipedId) {
        Date unmatchedAt = doUnmatch(swiperId, swipedId);
        if (!unmatchAuditDAO.existsBy(swiperId, swipedId)) {
            Account swiper = accountDAO.findById(swiperId, false);
            Account swiped = accountDAO.findById(swipedId, false);
            if (swiped == null) {
                throw new SwipedNotFoundException();
            }
            UnmatchAudit unmatchAudit = new UnmatchAudit(swiper, swiped, unmatchedAt);
            unmatchAuditDAO.persist(unmatchAudit);
        }
        return new UnmatchDTO(unmatchedAt);
    }

    @Override
    @Transactional
    public ReportMatchDTO reportMatch(UUID reporterId, UUID reportedId, int reportReasonId, String description) {
        Date reportedAt = doUnmatch(reporterId, reportedId);
        reportService.createReport(reporterId, reportedId, reportReasonId, description, reportedAt);
        return new ReportMatchDTO(reportedAt);
    }

    @SuppressWarnings("Duplicates")
    private Date doUnmatch(UUID swiperId, UUID swipedId) {
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

        Date now = new Date();
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
        return now;
    }
}

