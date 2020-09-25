package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CardDTO {

    private AccountProfileDTO card;
    private List<PhotoDTO> photos = new ArrayList<>();
}
