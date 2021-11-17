package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;
import com.beeswork.balanceaccountservice.exception.login.InvalidSocialLoginException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class GoogleLoginServiceImpl implements GoogleLoginService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Autowired
    public GoogleLoginServiceImpl(GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    @Override
    public VerifySocialLoginDTO verifyLogin(String loginId, String idToken) throws GeneralSecurityException, IOException {
        VerifySocialLoginDTO verifySocialLoginDTO = new VerifySocialLoginDTO(false);
        if (StringUtils.isBlank(loginId) || StringUtils.isBlank(idToken)) return verifySocialLoginDTO;

        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        if (googleIdToken == null) return verifySocialLoginDTO;

        String subject = googleIdToken.getPayload().getSubject();
        if (!loginId.equals(subject)) return verifySocialLoginDTO;

        String email = googleIdToken.getPayload().getEmail();
        verifySocialLoginDTO.setVerified(true);
        verifySocialLoginDTO.setEmail(email);
        verifySocialLoginDTO.setSocialLoginId(subject);
        return verifySocialLoginDTO;
    }
}
