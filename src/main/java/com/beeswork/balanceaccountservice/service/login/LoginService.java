package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.dto.login.RefreshAccessTokenDTO;

import java.util.UUID;

public interface LoginService {
    LoginDTO login(String loginId, LoginType loginType, String email, String password);
    LoginDTO socialLogin(String loginId, String email, LoginType loginType);
    void saveEmail(UUID accountId, UUID identityToken, String email);
    String getEmail(UUID accountId, UUID identityToken);
    RefreshAccessTokenDTO refreshAccessToken(UUID accountId, String refreshToken);
    LoginDTO loginWithRefreshToken(UUID accountId, String refreshToken);
}
