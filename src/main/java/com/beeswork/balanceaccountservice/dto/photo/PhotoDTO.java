package com.beeswork.balanceaccountservice.dto.photo;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PhotoDTO {
    private UUID accountId;
    private String key;
    private int sequence;
}
