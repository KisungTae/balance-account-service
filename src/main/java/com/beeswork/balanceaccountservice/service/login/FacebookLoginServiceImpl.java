package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;
import org.springframework.stereotype.Service;

@Service
public class FacebookLoginServiceImpl implements FacebookLoginService {
    @Override
    public VerifyLoginDTO verifyLogin(String loginId, String accessToken) {
        return null;
    }
}
