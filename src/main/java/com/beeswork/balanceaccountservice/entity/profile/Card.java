package com.beeswork.balanceaccountservice.entity.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Card {

    private UUID         accountId;
    private String       name;
    private Integer      birthYear;
    private Integer      height;
    private String       about;
    private Double       distance;
    private String       photoKey;
    private List<String> photoKeys = new ArrayList<>();

    public Card(UUID accountId, String name, Integer birthYear, Integer height, String about, Double distance, String photoKey) {
        this.accountId = accountId;
        this.name = name;
        this.birthYear = birthYear;
        this.height = height;
        this.about = about;
        this.distance = distance;
        this.photoKey = photoKey;
    }
}
