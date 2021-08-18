package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.chat.SentChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
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

    @Autowired
    public MatchServiceImpl(AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            ChatMessageDAO chatMessageDAO,
                            SentChatMessageDAO sentChatMessageDAO) {
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.sentChatMessageDAO = sentChatMessageDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListMatchesDTO listMatches(UUID accountId, Date fetchedAt) {
        ListMatchesDTO listMatchesDTO = new ListMatchesDTO(fetchedAt);
        List<MatchDTO> matchDTOs = matchDAO.findAllAfter(accountId, offsetFetchedAt(fetchedAt));

        if (matchDTOs != null) {
            for (MatchDTO matchDTO : matchDTOs) {
                if (matchDTO.getUnmatched() || matchDTO.getDeleted()) {
                    matchDTO.setProfilePhotoKey(null);
                    matchDTO.setCreatedAt(null);
                    matchDTO.setActive(true);
                    matchDTO.setUnmatched(true);
                }
                if (matchDTO.getUpdatedAt().after(listMatchesDTO.getFetchedAt()))
                    listMatchesDTO.setFetchedAt(matchDTO.getUpdatedAt());
                matchDTO.setUpdatedAt(null);
                matchDTO.setDeleted(null);
            }
        }
        listMatchesDTO.setMatchDTOs(matchDTOs);
        return listMatchesDTO;
    }

    @Override
    @Transactional
    public void unmatch(UUID accountId, UUID swipedId) {
        Match swiperMatch = matchDAO.findById(accountId, swipedId);
        Match swipedMatch = matchDAO.findById(swipedId, accountId);

        if (swiperMatch == null || swipedMatch == null) throw new MatchNotFoundException();

        Date updatedAt = new Date();
        if (swiperMatch.isUnmatched()) {
            swiperMatch.setDeleted(true);
            swiperMatch.setUpdatedAt(updatedAt);
        } else {
            swiperMatch.setupAsUnmatcher(updatedAt);
            swipedMatch.setupAsUnmatched(updatedAt);
        }
    }
}

