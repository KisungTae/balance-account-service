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
@Setter
public class ListMatchDTO {
    private List<MatchDTO> matchDTOs = new ArrayList<>();
    private List<ChatMessageDTO> chatMessageDTOs = new ArrayList<>();
    private Date lastAccountUpdatedAt;
}
