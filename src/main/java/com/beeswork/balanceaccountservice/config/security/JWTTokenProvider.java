package com.beeswork.balanceaccountservice.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface JWTTokenProvider {
    Jws<Claims> parseJWTToken(String jwtToken);
    void validateJWTToken(Jws<Claims> jws);
    Authentication getAuthentication(Jws<Claims> jws);
    UUID getUserName(Jws<Claims> jws);
    String createRefreshToken(String userName, String key, Date date);
    String createAccessToken(String userName, List<String> roles);
    String resolveAccessToken(HttpServletRequest httpServletRequest);
    UUID getRefreshTokenKey(Jws<Claims> jws);
    Date getExpirationDate(Jws<Claims> jws);
    boolean shouldReissueRefreshToken(Jws<Claims> jws);



    void validateAuthentication(String accessToken);
}
