package com.beeswork.balanceaccountservice.vm.account;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AccountQuestionVM {
    private long questionId;
    private int sequence;
    private boolean selected;
}
