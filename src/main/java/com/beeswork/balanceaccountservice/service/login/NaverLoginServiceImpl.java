package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;
import org.springframework.stereotype.Service;

@Service
public class NaverLoginServiceImpl implements NaverLoginService {
    @Override
    public VerifySocialLoginDTO verifyLogin(String loginId, String accessToken) {
        return null;
    }
}
