package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountDTO extends AccountProfileDTO {

    private String id;
    private String email;
    private String name;
    private Date birth;
    private String about;
    private boolean gender;
    private double latitude;
    private double longitude;
    private List<AccountQuestionDTO> accountQuestionDTOs = new ArrayList<>();
}
