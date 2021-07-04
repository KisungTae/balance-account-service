package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleLoginService {
    VerifyLoginDTO verifyLogin(String loginId, String idToken) throws GeneralSecurityException, IOException;
}
