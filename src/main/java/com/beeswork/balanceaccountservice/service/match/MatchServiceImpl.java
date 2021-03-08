package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.chat.SentChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
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
    private final AccountDAO accountDAO;
    private final MatchDAO matchDAO;
    private final ChatMessageDAO chatMessageDAO;
    private final SentChatMessageDAO sentChatMessageDAO;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            ChatMessageDAO chatMessageDAO,
                            SentChatMessageDAO sentChatMessageDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.sentChatMessageDAO = sentChatMessageDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListMatchesDTO listMatches(UUID accountId, UUID identityToken, Date fetchedAt) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        ListMatchesDTO listMatchesDTO = new ListMatchesDTO(fetchedAt);
        List<MatchDTO> matchDTOs = matchDAO.findAllAfter(accountId, offsetFetchedAt(fetchedAt));

        if (matchDTOs != null) {
            for (MatchDTO matchDTO : matchDTOs) {
                if (matchDTO.isUnmatched() || matchDTO.isDeleted()) {
                    matchDTO.setRepPhotoKey(null);
                    matchDTO.setCreatedAt(null);
                    matchDTO.setActive(true);
                }
                if (matchDTO.getUpdatedAt().after(listMatchesDTO.getFetchedAt()))
                    listMatchesDTO.setFetchedAt(matchDTO.getUpdatedAt());
                matchDTO.setUpdatedAt(null);
            }
        }
        listMatchesDTO.setMatchDTOs(matchDTOs);
        listMatchesDTO.setReceivedChatMessageDTOs(chatMessageDAO.findAllUnreceived(accountId));
        listMatchesDTO.setSentChatMessageDTOs(sentChatMessageDAO.findAllUnfetched(accountId));
        return listMatchesDTO;
    }

    @Override
    @Transactional
    public void unmatch(UUID accountId, UUID identityToken, UUID unmatchedId) {
        Match matcherMatch = matchDAO.findById(accountId, unmatchedId);
        Match matchedMatch = matchDAO.findById(unmatchedId, accountId);

        if (matcherMatch == null || matchedMatch == null)
            throw new MatchNotFoundException();

        if (!matcherMatch.getMatcher().getIdentityToken().equals(identityToken))
            throw new AccountNotFoundException();

        Date updatedAt = new Date();
        matcherMatch.setUnmatched(true);
        matcherMatch.setUnmatcher(true);
        matcherMatch.setDeleted(true);
        matcherMatch.setUpdatedAt(updatedAt);

        matchedMatch.setUnmatched(true);
        matchedMatch.setUnmatcher(false);
        matchedMatch.setUpdatedAt(updatedAt);
    }

}

