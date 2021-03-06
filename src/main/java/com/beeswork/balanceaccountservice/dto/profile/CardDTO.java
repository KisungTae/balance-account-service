package com.beeswork.balanceaccountservice.dto.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CardDTO {
    private String       accountId;
    private String       name;
    private int          birthYear;
    private Integer      height;
    private String       about;
    private int          distance;
    private List<String> photoKeys = new ArrayList<>();

    public CardDTO(String accountId,
                   String name,
                   int birthYear,
                   Integer height,
                   String about,
                   int distance) {
        this.accountId = accountId;
        this.name = name;
        this.birthYear = birthYear;
        this.height = height;
        this.about = about;
        this.distance = distance;
    }
}
