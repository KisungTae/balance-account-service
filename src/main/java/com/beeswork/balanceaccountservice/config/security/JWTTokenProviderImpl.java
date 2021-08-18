package com.beeswork.balanceaccountservice.config.security;

import com.beeswork.balanceaccountservice.constant.HttpHeader;
import com.beeswork.balanceaccountservice.exception.login.RefreshTokenNotFoundException;
import com.beeswork.balanceaccountservice.service.login.LoginService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
public class JWTTokenProviderImpl implements JWTTokenProvider {

    //  TODO: secretkey should be from credential.yaml
    private       String             secretKey                = "a8300909-ece3-4cc5-ac66-12883a8eb452";
    //    private final long ACCESS_TOKEN_VALID_TIME = 5 * 60 * 60 * 1000L;
    private final long               ACCESS_TOKEN_VALID_TIME  = 5 * 60 * 1000L;
    private final long               REFRESH_TOKEN_VALID_TIME = 14 * 24 * 60 * 60 * 1000L;
    private final String             REFRESH_TOKEN_KEY        = "key";
    private final String             ACCESS_TOKEN_ROLES       = "roles";

    private final LoginService loginService;

    @Autowired
    public JWTTokenProviderImpl(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createRefreshToken(String userName, String key) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(REFRESH_TOKEN_KEY, key);
        Date now = new Date();
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    public String createAccessToken(String userName, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(ACCESS_TOKEN_ROLES, roles);
        Date now = new Date();
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    public Authentication getAuthentication(String token, String identityToken) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName(token));
        UserDetails userDetails = loginService.loadUserByUsername(getUserName(token), identityToken);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String getUserName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveAccessToken(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(HttpHeader.ACCESS_TOKEN);
    }

    public String getRefreshTokenKey(String token) {
        Object refreshTokenKey = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(REFRESH_TOKEN_KEY);
        if (refreshTokenKey == null) throw new RefreshTokenNotFoundException();
        return refreshTokenKey.toString();
    }

    public boolean validateAccessToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return !claims.getBody().getExpiration().before(new Date());
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            return validateAccessToken(token);
        } catch (Exception e) {
            return false;
        }
    }


}
