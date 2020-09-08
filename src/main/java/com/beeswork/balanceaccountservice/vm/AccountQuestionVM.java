package com.beeswork.balanceaccountservice.vm;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AccountQuestionVM {
    private long accountId;
    private long questionId;
    private int sequence;
    private boolean selected;
}
