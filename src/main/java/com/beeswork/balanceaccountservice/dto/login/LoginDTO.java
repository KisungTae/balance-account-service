package com.beeswork.balanceaccountservice.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private UUID    accountId;
    private boolean profileExists;
    private String  accessToken;
    private String  refreshToken;
    private String email;
    private String photoBucketUrl;

    public LoginDTO(UUID accountId, boolean profileExists, String accessToken, String refreshToken, String photoBucketUrl) {
        this.accountId = accountId;
        this.profileExists = profileExists;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.photoBucketUrl = photoBucketUrl;
    }
}
