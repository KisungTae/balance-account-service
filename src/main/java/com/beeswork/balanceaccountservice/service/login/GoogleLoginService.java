package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleLoginService {
    VerifySocialLoginDTO verifyLogin(String loginId, String idToken) throws GeneralSecurityException, IOException;
}
