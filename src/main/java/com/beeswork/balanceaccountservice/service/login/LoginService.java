package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.dto.login.RefreshAccessTokenDTO;

public interface LoginService {
    LoginDTO login(String loginId, LoginType loginType, String email, String password);
    LoginDTO socialLogin(String loginId, String email, LoginType loginType);
    RefreshAccessTokenDTO refreshAccessToken(String accessToken, String refreshToken, boolean includeAccountId);
    LoginDTO loginWithRefreshToken(String accessToken, String refreshToken);
}
