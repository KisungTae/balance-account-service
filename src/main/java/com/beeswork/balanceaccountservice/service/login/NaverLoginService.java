package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;

public interface NaverLoginService {
    VerifyLoginDTO verifyLogin(String loginId, String accessToken);
}
