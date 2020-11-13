package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProfileDTO {
    private String name;
    private Integer height;
    private Date birth;
    private String about;
    private boolean gender;
    private int point;
    private String email;
}
