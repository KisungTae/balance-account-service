package com.beeswork.balanceaccountservice.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshAccessTokenDTO {
    private String accessToken;
    private String refreshToken;
}
