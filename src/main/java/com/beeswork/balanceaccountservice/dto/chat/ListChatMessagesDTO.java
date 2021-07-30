package com.beeswork.balanceaccountservice.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListChatMessagesDTO {
    private List<ChatMessageDTO> sentChatMessageDTOs;
    private List<ChatMessageDTO> receivedChatMessageDTOs;
}
