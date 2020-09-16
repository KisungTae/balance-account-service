package com.beeswork.balanceaccountservice.dto.match;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchDTO {

    private String matchedId;
    private boolean isMatched;
    private String matchedImageUrl;

}
