package com.beeswork.balanceaccountservice.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RefreshAccessTokenDTO {
    private String accessToken;
    private String refreshToken;
    private UUID accountId;

    public RefreshAccessTokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
