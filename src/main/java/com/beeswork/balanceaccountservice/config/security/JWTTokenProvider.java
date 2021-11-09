package com.beeswork.balanceaccountservice.config.security;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

public interface JWTTokenProvider {
    String createRefreshToken(String userName, String key);
    String createAccessToken(String userName, List<String> roles);
    Authentication getAuthentication(String accessToken, String identityToken);
    String getUserName(String token);
    String resolveAccessToken(HttpServletRequest httpServletRequest);
    UUID getRefreshTokenKey(String refreshToken);
    boolean validateAccessToken(String accessToken);
    boolean validateRefreshToken(String refreshToken);
    void validateAuthentication(String accessToken, String identityToken);
}
