package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class ListMatchDTO {
    private List<MatchDTO> matchDTOs = new ArrayList<>();
    private List<ChatMessageDTO> sentChatMessageDTOs = new ArrayList<>();
    private List<ChatMessageDTO> receivedChatMessageDTOs = new ArrayList<>();

    public void setMatchDTOs(List<MatchDTO> matchDTOs) {
        if (matchDTOs != null) this.matchDTOs = matchDTOs;
    }

    public void setSentChatMessageDTOs(List<ChatMessageDTO> sentChatMessageDTOs) {
        if (sentChatMessageDTOs != null) this.sentChatMessageDTOs = sentChatMessageDTOs;
    }

    public void setReceivedChatMessageDTOs(List<ChatMessageDTO> receivedChatMessageDTOs) {
        if (receivedChatMessageDTOs != null) this.receivedChatMessageDTOs = receivedChatMessageDTOs;
    }
}
