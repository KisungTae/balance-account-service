package com.beeswork.balanceaccountservice.dto.profile;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProfileDTO {
    private String name;
    private Integer height;
    private Date birth;
    private String about;
    private boolean gender;
}
