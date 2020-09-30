package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardDTO {

    private String accountId;
    private String name;
    private String about;
    private int birthYear;
    private int distance;

    private List<String> photos = new ArrayList<>();

    public CardDTO(String accountId, String name, String about, int birthYear, int distance) {
        this.accountId = accountId;
        this.name = name;
        this.about = about;
        this.birthYear = birthYear;
        this.distance = distance;
    }
}
