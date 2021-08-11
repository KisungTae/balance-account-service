package com.beeswork.balanceaccountservice.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import reactor.netty.http.server.HttpServerRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JWTTokenProvider {

    private       String             secretKey      = "a8300909-ece3-4cc5-ac66-12883a8eb452";
    private final long ACCESS_TOKEN_VALID_TIME = 5 * 60 * 60 * 1000L;
    private final long REFRESH_TOKEN_VALID_TIME =  14 * 24 * 60 * 60 * 1000L;
    private final String ACCESS_TOKEN = "ACCESS-TOKEN";
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createRefreshToken(String userName, String value) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("value", value);
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
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String getUserName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveAccessToken(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(ACCESS_TOKEN);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println("excepiton validate token: " + e.getLocalizedMessage());
            return false;
        }
    }
}
