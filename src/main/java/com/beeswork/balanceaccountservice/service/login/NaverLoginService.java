package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;

public interface NaverLoginService {
    VerifySocialLoginDTO verifyLogin(String loginId, String accessToken);
}
