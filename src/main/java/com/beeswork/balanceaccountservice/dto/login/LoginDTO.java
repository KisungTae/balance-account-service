package com.beeswork.balanceaccountservice.dto.login;

import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
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
    private String  email;
    private String   photoURLDomain;
    private PhotoDTO profilePhotoDTO;

    public LoginDTO(UUID accountId, boolean profileExists, String accessToken, String refreshToken, String photoURLDomain) {
        this.accountId = accountId;
        this.profileExists = profileExists;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.photoURLDomain = photoURLDomain;
    }
}
