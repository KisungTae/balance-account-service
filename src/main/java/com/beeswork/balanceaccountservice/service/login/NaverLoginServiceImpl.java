package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;
import org.springframework.stereotype.Service;

@Service
public class NaverLoginServiceImpl implements NaverLoginService {
    @Override
    public VerifyLoginDTO verifyLogin(String loginId, String accessToken) {
        return null;
    }
}
