package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AccountProfileListDTO {

    private String urlPrefix;
    private List<AccountProfileListDTO> accountProfileListDTOs = new ArrayList<>();
}
