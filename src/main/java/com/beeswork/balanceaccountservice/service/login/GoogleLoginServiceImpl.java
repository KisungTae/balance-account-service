package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.config.properties.GoogleLoginProperties;
import com.beeswork.balanceaccountservice.constant.LoginType;
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

    private final GoogleLoginProperties googleLoginProperties;

    @Autowired
    public GoogleLoginServiceImpl(GoogleLoginProperties googleLoginProperties) {
        this.googleLoginProperties = googleLoginProperties;
    }

    @Override
    public String validate(String loginId, String idToken) throws GeneralSecurityException, IOException {
        if (loginId.isEmpty() || idToken.isEmpty()) throw new InvalidSocialLoginException();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleLoginProperties.getClientId())).build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) throw new InvalidSocialLoginException();
        else {
            String subject = googleIdToken.getPayload().getSubject();
            if (!loginId.equals(subject)) throw new InvalidSocialLoginException();
            return googleIdToken.getPayload().getSubject();
        }
    }
}
