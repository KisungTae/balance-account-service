package com.beeswork.balanceaccountservice.config.security;

import com.beeswork.balanceaccountservice.config.properties.JWTTokenProperties;
import com.beeswork.balanceaccountservice.constant.HttpHeader;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidJWTTokenException;
import com.beeswork.balanceaccountservice.exception.login.InvalidRefreshTokenException;
import com.beeswork.balanceaccountservice.service.security.UserDetailService;
import com.beeswork.balanceaccountservice.util.Convert;
import com.google.common.base.Strings;
import io.jsonwebtoken.*;
import io.micrometer.core.instrument.util.StringUtils;
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
import java.util.UUID;


@Component
public class JWTTokenProviderImpl implements JWTTokenProvider {

    //    private final long   ACCESS_TOKEN_LIFE_TIME  = 60 * 60 * 1000L;
    private final long   ACCESS_TOKEN_LIFE_TIME  = 1 * 60 * 1000L;
    private final long   REFRESH_TOKEN_LIFE_TIME = 14 * 24 * 60 * 60 * 1000L;
    private final String REFRESH_TOKEN_KEY       = "key";
    private final String ACCESS_TOKEN_ROLES      = "roles";
    private final int REFRESH_TOKEN_RESET_TIME = 2;
    private       String secretKey;

    private final JWTTokenProperties jwtTokenProperties;
    private final UserDetailService  userDetailsService;

    @Autowired
    public JWTTokenProviderImpl(JWTTokenProperties jwtTokenProperties,
                                UserDetailService userDetailsService) {
        this.jwtTokenProperties = jwtTokenProperties;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtTokenProperties.getSecretKey().getBytes());
    }

    @Override
    public Jws<Claims> parseJWTToken(String token) {
        try {
            if (StringUtils.isBlank(token)) throw new InvalidJWTTokenException();
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new InvalidJWTTokenException();
        }
    }

    @Override
    public void validateJWTToken(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) throw new InvalidJWTTokenException();
        else if (claims.getExpiration() == null) throw new InvalidJWTTokenException();
        else if (!claims.getExpiration().before(new Date())) throw new ExpiredJwtException(null, claims, "");
    }

    @Override
    public Authentication getAuthentication(Jws<Claims> jws, UUID identityToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName(jws), identityToken);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public UUID getUserName(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) return null;
        String userName = claims.getSubject();
        return Convert.toUUID(userName);
    }

    @Override
    public String createRefreshToken(String userName, String key, Date issuedAt) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(REFRESH_TOKEN_KEY, key);
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(issuedAt)
                   .setExpiration(new Date(issuedAt.getTime() + REFRESH_TOKEN_LIFE_TIME))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    @Override
    public String createAccessToken(String userName, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(ACCESS_TOKEN_ROLES, roles);
        Date now = new Date();
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_LIFE_TIME))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    @Override
    public String resolveAccessToken(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(HttpHeader.ACCESS_TOKEN);
    }

    @Override
    public UUID getRefreshTokenKey(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) throw new InvalidJWTTokenException();
        Object refreshTokenKey = claims.get(REFRESH_TOKEN_KEY);
        if (refreshTokenKey == null) throw new InvalidJWTTokenException();
        return Convert.toUUIDOrThrow(refreshTokenKey.toString(), new InvalidJWTTokenException());
    }

    @Override
    public boolean shouldCreateNewRefreshToken(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) throw new InvalidJWTTokenException();
        Date expiration = claims.getExpiration();



        return false;
    }


    @Override
    public UUID getRefreshTokenKey(String refreshToken) {
        Object refreshTokenKey = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().get(REFRESH_TOKEN_KEY);
        if (refreshTokenKey == null) throw new InvalidRefreshTokenException();
        return Convert.toUUIDOrThrow(refreshTokenKey.toString(), new InvalidRefreshTokenException());
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            return validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateToken(String token) {
        if (token.isEmpty()) return false;
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return !claims.getBody().getExpiration().before(new Date());
    }

    public Date getExpirationFromRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
            return claims.getBody().getExpiration();
        } catch (Exception e) {
            throw new InvalidRefreshTokenException();
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            return validateToken(refreshToken);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void validateAuthentication(String accessToken, String identityToken) {
//        userDetailsService.loadUserByUsername(getUserName(accessToken), identityToken);
    }


}
