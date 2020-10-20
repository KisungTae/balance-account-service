package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class LocationDTO {

    private String accountId;
    private String email;
    private double latitude;
    private double longitude;
}
