package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class AccountQuestionSaveDTO {
    private UUID accountId;
    private List<AccountQuestionDTO> accountQuestionDTOs = new ArrayList<>();
}
