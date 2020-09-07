package com.beeswork.balanceaccountservice.dto;

import com.beeswork.balanceaccountservice.vm.AccountQuestionVM;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AccountDTO {

    private int id;
    private String email;
    private String name;
    private int birth;
    private String about;
    private boolean gender;
    private double latitude;
    private double longitude;
    private List<AccountQuestionVM> accountQuestionVMs;
}
