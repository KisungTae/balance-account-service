package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListMatchDTO {

    private Date                 fetchedAt;
    private List<MatchDTO>       matchDTOs;
    private List<ChatMessageDTO> sentChatMessageDTOs;
    private List<ChatMessageDTO> receivedChatMessageDTOs;

    public ListMatchDTO(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
