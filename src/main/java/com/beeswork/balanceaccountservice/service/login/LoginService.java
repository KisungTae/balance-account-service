package com.beeswork.balanceaccountservice.service.login;

import java.util.UUID;

public interface LoginService {
    void saveEmail(UUID accountId, UUID identityToken, String email);
}
