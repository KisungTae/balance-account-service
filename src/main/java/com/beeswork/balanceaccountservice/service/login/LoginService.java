package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dto.login.CreateLoginDTO;

import java.util.UUID;

public interface LoginService {
    CreateLoginDTO createLogin(String loginId, LoginType loginType);
    void saveEmail(UUID accountId, UUID identityToken, String email);
    String getEmail(UUID accountId, UUID identityToken);
}
