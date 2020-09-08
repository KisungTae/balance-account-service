package com.beeswork.balanceaccountservice.dto.account;

import com.beeswork.balanceaccountservice.dto.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.vm.AccountQuestionVM;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountDTO {

    private UUID id;
    private String email;
    private String name;
    private int birth;
    private String about;
    private boolean gender;
    private double latitude;
    private double longitude;
    private List<AccountQuestionDTO> accountQuestionDTOs = new ArrayList<>();
}
