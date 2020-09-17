package com.beeswork.balanceaccountservice.dto.account;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class AccountProfileDTO {

    private String id;
    private String name;
    private String about;
    private int birthYear;
    private int distance;
    private List<PhotoDTO> photoDTOs = new ArrayList<>();

    public AccountProfileDTO(String id, String name, String about, int birthYear, int distance) {
        this.id = id;
        this.name = name;
        this.about = about;
        this.birthYear = birthYear;
        this.distance = distance;
    }
}
