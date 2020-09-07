package com.beeswork.balanceaccountservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountQuestionDTO {
    private long accountId;
    private long questionId;
    private boolean selected;
}
