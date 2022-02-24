package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListMatchesDTO {
    private List<MatchDTO> matchDTOs;
    private long           matchCount;
    private Date           matchCountCountedAt;
    private long           swipeCount;
    private Date           swipeCountCountedAt;
}
