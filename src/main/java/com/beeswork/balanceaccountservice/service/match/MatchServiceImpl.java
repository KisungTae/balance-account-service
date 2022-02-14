package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.chat.SentChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.match.UnmatchAuditDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.UnmatchAudit;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
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
    private final ChatMessageDAO     chatMessageDAO;
    private final SentChatMessageDAO sentChatMessageDAO;
    private final UnmatchAuditDAO unmatchAuditDAO;

    @Autowired
    public MatchServiceImpl(AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            ChatMessageDAO chatMessageDAO,
                            SentChatMessageDAO sentChatMessageDAO,
                            UnmatchAuditDAO unmatchAuditDAO) {
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.sentChatMessageDAO = sentChatMessageDAO;
        this.unmatchAuditDAO = unmatchAuditDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListMatchesDTO listMatches(UUID accountId, Date fetchedAt) {
        ListMatchesDTO listMatchesDTO = new ListMatchesDTO(fetchedAt);
        List<MatchDTO> matchDTOs = matchDAO.findAllAfter(accountId, offsetFetchedAt(fetchedAt));

        if (matchDTOs != null) {
            for (MatchDTO matchDTO : matchDTOs) {
                if (matchDTO.getUnmatched() || matchDTO.getDeleted()) {
                    matchDTO.setSwipedProfilePhotoKey(null);
//                    matchDTO.setCreatedAt(null);
                    matchDTO.setActive(true);
                    matchDTO.setUnmatched(true);
                }
//                if (matchDTO.getUpdatedAt().after(listMatchesDTO.getFetchedAt())) {
//                    listMatchesDTO.setFetchedAt(matchDTO.getUpdatedAt());
//                }

//                matchDTO.setUpdatedAt(null);
                matchDTO.setDeleted(null);
            }
        }
        listMatchesDTO.setMatchDTOs(matchDTOs);
        return listMatchesDTO;
    }

    @Override
    @Transactional
    @SuppressWarnings("Duplicates")
    public void unmatch(UUID swiperId, UUID swipedId) {

        // NOTE 1. Even if you fetch an entity with writeLock,
        //         you can still write on the entity if you fetch it without writeLock on another thread

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

        if (!unmatchAuditDAO.existsBy(swiperId, swipedId)) {
            UnmatchAudit unmatchAudit = new UnmatchAudit(swiperMatch.getSwiper(), swiperMatch.getSwiped(), now);
            unmatchAuditDAO.persist(unmatchAudit);
        }
    }
}

