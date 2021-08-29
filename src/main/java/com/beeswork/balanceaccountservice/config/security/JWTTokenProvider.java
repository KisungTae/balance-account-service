package com.beeswork.balanceaccountservice.config.security;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface JWTTokenProvider {
    String createRefreshToken(String userName, String key);
    String createAccessToken(String userName, List<String> roles);
    Authentication getAuthentication(String token, String identityToken);
    String getUserName(String token);
    String resolveAccessToken(HttpServletRequest httpServletRequest);
    String getRefreshTokenKey(String token);
    boolean validateAccessToken(String token);
    boolean validateRefreshToken(String token);
    void validateAuthentication(String token, String identityToken);
}
