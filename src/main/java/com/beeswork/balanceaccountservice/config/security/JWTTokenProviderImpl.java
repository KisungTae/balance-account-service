package com.beeswork.balanceaccountservice.config.security;

import com.beeswork.balanceaccountservice.config.properties.JWTTokenProperties;
import com.beeswork.balanceaccountservice.constant.HttpHeader;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidJWTTokenException;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidRefreshTokenException;
import com.beeswork.balanceaccountservice.service.security.UserDetailService;
import com.beeswork.balanceaccountservice.util.Convert;
import io.jsonwebtoken.*;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Component
public class JWTTokenProviderImpl implements JWTTokenProvider {

    //    private final long   ACCESS_TOKEN_LIFE_TIME     = 60 * 60 * 1000L;
    private final long   ACCESS_TOKEN_LIFE_TIME     = 60 * 1000L;
    private final long   REFRESH_TOKEN_LIFE_TIME    = 14 * 24 * 60 * 60 * 1000L;
    private final String REFRESH_TOKEN_KEY          = "key";
    private final String ACCESS_TOKEN_ROLES         = "roles";
    private final long   REFRESH_TOKEN_REISSUE_TIME = 2 * 24 * 60 * 60 * 1000L;
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
    public Jws<Claims> parseJWTToken(String jwtToken) {
        try {
            if (StringUtils.isBlank(jwtToken)) throw new InvalidJWTTokenException();
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new InvalidJWTTokenException();
        }
    }

    @Override
    public void validateJWTToken(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) throw new InvalidJWTTokenException();
        else if (claims.getExpiration() == null) throw new InvalidJWTTokenException();
        else if (claims.getExpiration().before(new Date())) throw new ExpiredJwtException(null, null, "");
    }

    @Override
    public Authentication getAuthentication(Jws<Claims> jws) {
        UserDetails userDetails = userDetailsService.loadValidUserByUsername(getUserName(jws));
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
        if (httpServletRequest == null) return null;
        return httpServletRequest.getHeader(HttpHeader.ACCESS_TOKEN);
    }

    @Override
    public UUID getRefreshTokenKey(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) return null;
        Object refreshTokenKey = claims.get(REFRESH_TOKEN_KEY);
        if (refreshTokenKey == null) return null;
        return Convert.toUUID(refreshTokenKey.toString());
    }

    @Override
    public Date getExpirationDate(Jws<Claims> jws) {
        Claims claims = jws.getBody();
        if (claims == null) return null;
        return claims.getExpiration();
    }

    @Override
    public boolean shouldReissueRefreshToken(Jws<Claims> jws) {
        Date expirationDate = getExpirationDate(jws);
        if (expirationDate == null) throw new InvalidRefreshTokenException();
        Date now = new Date();
        long diff = expirationDate.getTime() - now.getTime();
        if (diff <= 0) throw new ExpiredJwtException(null, null, "");
        return diff <= REFRESH_TOKEN_REISSUE_TIME;
    }


    @Override
    public void validateAuthentication(String accessToken) {
//        userDetailsService.loadUserByUsername(getUserName(accessToken), identityToken);
    }


}
