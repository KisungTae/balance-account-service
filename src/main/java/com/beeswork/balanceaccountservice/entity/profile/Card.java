package com.beeswork.balanceaccountservice.entity.profile;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Card {

    private UUID         accountId;
    private String       name;
    private Boolean      gender;
    private Integer      birthYear;
    private Integer      height;
    private String       about;
    private Double       distance;
    private String       photoKey;
    private List<String> photoKeys = new ArrayList<>();

    public Card(UUID accountId, String name, Boolean gender, Integer birthYear, Integer height, String about, Double distance, String photoKey) {
        this.accountId = accountId;
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.height = height;
        this.about = about;
        this.distance = distance;
        this.photoKey = photoKey;
    }
}
