package com.beeswork.balanceaccountservice.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifySocialLoginDTO {
    private String socialLoginId;
    private String email;
    private boolean isVerified;

    public VerifySocialLoginDTO(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
