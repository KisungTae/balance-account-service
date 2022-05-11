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
    private Boolean      gender;
    private Integer      birthYear;
    private Integer      height;
    private String       about;
    private Integer      distance;
    private List<String> photoKeys = new ArrayList<>();
}
