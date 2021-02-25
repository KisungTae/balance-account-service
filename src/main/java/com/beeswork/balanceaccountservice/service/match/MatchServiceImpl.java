package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchDTO;
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
    private final  AccountDAO     accountDAO;
    private final  MatchDAO       matchDAO;
    private final  ChatMessageDAO chatMessageDAO;

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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListMatchDTO listMatches(UUID accountId, UUID identityToken, Date fetchedAt) {
//        checkIfAccountValid(accountDAO.findById(accountId), identityToken);
        ListMatchDTO listMatchDTO = new ListMatchDTO(fetchedAt);

        List<MatchDTO> matchDTOs = matchDAO.findAllAfter(accountId, offsetFetchedAt(fetchedAt));
        for (MatchDTO matchDTO : matchDTOs) {
            if (matchDTO.getUpdatedAt().after(listMatchDTO.getFetchedAt()))
                listMatchDTO.setFetchedAt(matchDTO.getUpdatedAt());
            matchDTO.setUpdatedAt(null);
        }
        listMatchDTO.setMatchDTOs(matchDTOs);
        listMatchDTO.setReceivedChatMessageDTOs(chatMessageDAO.findAllUnreceived(accountId));
        listMatchDTO.setSentChatMessageDTOs(chatMessageDAO.findAllUnfetched(accountId));
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
        matcherMatch.setDeleted(true);
        matcherMatch.setUpdatedAt(updatedAt);

        matchedMatch.setUnmatched(true);
        matchedMatch.setUnmatcher(false);
        matchedMatch.setUpdatedAt(updatedAt);
    }


//    private void setResponseLimitSize() throws JsonProcessingException {
//        ListMatchDTO listMatchDTO = new ListMatchDTO();
//        listMatchDTO.setMatchFetchedAt(new Date());
//
//        emptyListMatchDTOJsonSize = objectMapper.writeValueAsString(listMatchDTO).getBytes().length;
//
//        listMatchDTO.setMatchDTOs(new ArrayList<>());
//        matchDTOListJsonSize = objectMapper.writeValueAsString(listMatchDTO)
//                                           .getBytes().length - emptyListMatchDTOJsonSize;
//
//        String name = "열글자면삼십바이트다";
//        String photoKey = "2011-10-05T14:48:00.000+00:00";
//        Long chatId = Long.MAX_VALUE;
//        UUID matchedId = UUID.randomUUID();
//        listMatchDTO.getMatchDTOs()
//                    .add(new MatchDTO(chatId, matchedId, false, name, photoKey, false, new Date(), null));
//        matchDTOJsonSize = objectMapper.writeValueAsString(listMatchDTO).getBytes().length - matchDTOListJsonSize - emptyListMatchDTOJsonSize;
//
//
//        listMatchDTO.setMatchDTOs(null);
//        listMatchDTO.setSentChatMessageDTOs(new ArrayList<>());
//        sentChatMessageDTOListJsonSize =
//                objectMapper.writeValueAsString(listMatchDTO).getBytes().length - emptyListMatchDTOJsonSize;
//
//        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
//        chatMessageDTO.setId(Long.MAX_VALUE);
//        chatMessageDTO.setMessageId(Long.MAX_VALUE);
//        chatMessageDTO.setCreatedAt(new Date());
//        listMatchDTO.getSentChatMessageDTOs().add(chatMessageDTO);
//        sentChatMessageDTOJsonSize =
//                objectMapper.writeValueAsString(listMatchDTO).getBytes().length - sentChatMessageDTOListJsonSize - emptyListMatchDTOJsonSize;
//
//
//        listMatchDTO.setSentChatMessageDTOs(null);
//        listMatchDTO.setReceivedChatMessageDTOs(new ArrayList<>());
//        receivedChatMessageDTOListJsonSize =
//                objectMapper.writeValueAsString(listMatchDTO).getBytes().length - emptyListMatchDTOJsonSize;
//
//
//        String body = "가".repeat(200);
//        List<ChatMessageDTO> received = new ArrayList<>();
//        received.add(new ChatMessageDTO(Long.MAX_VALUE, body, Long.MAX_VALUE, new Date()));
//        listMatchDTO.setReceivedChatMessageDTOs(received);
//        receivedChatMessageDTOJsonSize =
//                objectMapper.writeValueAsString(received).getBytes().length - receivedChatMessageDTOListJsonSize - emptyListMatchDTOJsonSize;
//    }

}

