package com.beeswork.balanceaccountservice.service.login;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleLoginService {
    String validate(String loginId, String idToken) throws GeneralSecurityException, IOException;
}
