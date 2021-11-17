package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;

public interface FacebookLoginService {
    VerifySocialLoginDTO verifyLogin(String loginId, String accessToken);
}
