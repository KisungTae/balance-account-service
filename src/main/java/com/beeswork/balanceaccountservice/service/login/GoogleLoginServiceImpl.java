package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.config.properties.GoogleLoginProperties;
import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;
import com.beeswork.balanceaccountservice.exception.login.InvalidSocialLoginException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleLoginServiceImpl implements GoogleLoginService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Autowired
    public GoogleLoginServiceImpl(GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    @Override
    public VerifyLoginDTO verifyLogin(String loginId, String idToken) throws GeneralSecurityException, IOException {
        if (loginId.isEmpty() || idToken.isEmpty()) throw new InvalidSocialLoginException();

        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        if (googleIdToken == null) throw new InvalidSocialLoginException();

        String subject = googleIdToken.getPayload().getSubject();
        String email = googleIdToken.getPayload().getEmail();
        if (!loginId.equals(subject)) throw new InvalidSocialLoginException();
        return new VerifyLoginDTO(subject, email);
    }
}
