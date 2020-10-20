package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LocationVM {

    @ValidUUID
    private String accountId;

    private String email;
    private double latitude;
    private double longitude;
}
