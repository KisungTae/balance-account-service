package com.beeswork.balanceaccountservice.dto.login;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private UUID    accountId;
    private UUID    identityToken;
    private boolean profileExists;
    private String  accessToken;
    private String  refreshToken;
    private String  email;



    public LoginDTO(UUID accountId, UUID identityToken, String email) {
        this.accountId = accountId;
        this.identityToken = identityToken;
        this.email = email;
    }

    public LoginDTO(UUID accountId, UUID identityToken, boolean profileExists, String accessToken, String refreshToken) {
        this.accountId = accountId;
        this.identityToken = identityToken;
        this.profileExists = profileExists;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
