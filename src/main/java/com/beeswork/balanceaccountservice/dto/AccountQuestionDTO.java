package com.beeswork.balanceaccountservice.dto;


import com.beeswork.balanceaccountservice.entity.AccountQuestion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountQuestionDTO {
    private long accountId;
    private long questionId;
    private int sequence;
    private boolean selected;

    public static AccountQuestionDTO findByQuestionId(List<AccountQuestionDTO> accountQuestionDTOs, long questionId) {
        return accountQuestionDTOs.stream().filter(a -> a.getQuestionId() == questionId).findFirst().orElse(null);
    }
}
