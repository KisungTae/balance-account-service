package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;
import org.springframework.stereotype.Service;

@Service
public class KakaoLoginServiceImpl implements KakaoLoginService {
    @Override
    public VerifySocialLoginDTO verifyLogin(String loginId, String accessToken) {
        return null;
    }
}
