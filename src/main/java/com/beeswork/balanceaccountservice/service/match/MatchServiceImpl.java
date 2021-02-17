package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chatmessage.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
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
import java.util.UUID;


@Service
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {
    private final AccountDAO accountDAO;
    private final MatchDAO matchDAO;
    private final ChatMessageDAO chatMessageDAO;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            ChatMessageDAO chatMessageDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
    }


    @Override
//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Transactional
    public ListMatchDTO listMatches(UUID accountId,
                                    UUID identityToken,
                                    Date matchFetchedAt,
                                    Date accountFetchedAt,
                                    Date chatMessageFetchedAt) {
        checkIfAccountValid(accountDAO.findById(accountId), identityToken);
        ListMatchDTO listMatchDTO = new ListMatchDTO();

        listMatchDTO.setMatchDTOs(matchDAO.findAllAfter(accountId,
                                                        offsetFetchedAt(matchFetchedAt),
                                                        offsetFetchedAt(accountFetchedAt)));
        listMatchDTO.setReceivedChatMessageDTOs(chatMessageDAO.findAllUnreceived(accountId, null));
        listMatchDTO.setSentChatMessageDTOs(chatMessageDAO.findAllSentAfter(accountId,
                                                                            offsetFetchedAt(chatMessageFetchedAt),
                                                                            null));
        return listMatchDTO;
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
        matcherMatch.setUpdatedAt(updatedAt);

        matchedMatch.setUnmatched(true);
        matchedMatch.setUnmatcher(false);
        matchedMatch.setUpdatedAt(updatedAt);
    }

}
