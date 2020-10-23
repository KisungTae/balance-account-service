package com.beeswork.balanceaccountservice.dto.account;


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

}
