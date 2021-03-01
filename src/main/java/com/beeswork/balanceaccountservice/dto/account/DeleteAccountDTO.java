package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteAccountDTO {
    private UUID accountId;
    private List<String> photoKeys = new ArrayList<>();
}
