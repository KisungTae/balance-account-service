package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListMatchDTO {

    private Date                 fetchedAt = new Date(Long.MIN_VALUE);
    private List<MatchDTO>       matchDTOs;
    private List<ChatMessageDTO> sentChatMessageDTOs;
    private List<ChatMessageDTO> receivedChatMessageDTOs;

}
