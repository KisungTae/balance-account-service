package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;
import org.springframework.stereotype.Service;

@Service
public class KakaoLoginServiceImpl implements KakaoLoginService {
    @Override
    public VerifyLoginDTO verifyLogin(String loginId, String accessToken) {
        return null;
    }
}
