package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.match.UnmatchAuditDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.match.CountMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.swipe.CountSwipesDTO;
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
    public ListMatchesDTO fetchMatches(final UUID swiperId, final UUID lastSwipedId, final int loadSize, final MatchPageFilter matchPageFilter) {
        return getListMatchesDTO(() -> doFetchMatches(swiperId, lastSwipedId, loadSize, matchPageFilter), swiperId);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<MatchDTO> doFetchMatches(UUID swiperId, UUID lastSwipedId, int loadSize, MatchPageFilter matchPageFilter) {
        List<MatchDTO> matchDTOs = matchDAO.findAllBy(swiperId, lastSwipedId, loadSize, matchPageFilter);
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

