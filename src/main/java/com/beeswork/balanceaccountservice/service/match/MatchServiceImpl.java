package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.querydsl.core.Tuple;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jca.cci.CciOperationNotSupportedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {

    private static final int CHAT_ID            = 0;
    private static final int MATCHED_ID         = 1;
    private static final int MATCH_UPDATED_AT   = 2;
    private static final int MATCH_UNMATCHED    = 3;
    private static final int NAME               = 4;
    private static final int REP_PHOTO_KEY      = 5;
    private static final int BLOCKED            = 6;
    private static final int DELETED            = 7;
    private static final int ACCOUNT_UPDATED_AT = 8;

    private final AccountDAO accountDAO;
    private final MatchDAO   matchDAO;
    private final ChatDAO    chatDAO;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            MatchDAO matchDAO,
                            ChatDAO chatDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
        this.chatDAO = chatDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<MatchProjection> listMatches(UUID accountId,
                                             UUID identityToken,
                                             Date lastAccountUpdatedAt,
                                             Date lastMatchUpdatedAt,
                                             Date lastChatMessageCreatedAt) {
//        checkIfAccountValid(accountDAO.findById(accountId), identityToken);
//        ListMatchDTO listMatchDTO = new ListMatchDTO();
//        listMatchDTO.setMatches(matchDAO.findAllAfter(accountId,
//                                                      DateUtils.addHours(lastAccountUpdatedAt, -1),
//                                                      DateUtils.addHours(lastMatchUpdatedAt, -1)));
//
//        if (listMatchDTO.getMatches().size() > 0)
//            listMatchDTO.setLastAccountUpdatedAt(listMatchDTO.getMatches().get(0).getAccountUpdatedAt());
//
//        for (MatchProjection match : listMatchDTO.getMatches()) {
//            if (match.getAccountUpdatedAt().after(listMatchDTO.getLastAccountUpdatedAt()))
//                listMatchDTO.setLastAccountUpdatedAt(match.getAccountUpdatedAt());
//            match.setAccountUpdatedAt(null);
//        }




        return null;
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
