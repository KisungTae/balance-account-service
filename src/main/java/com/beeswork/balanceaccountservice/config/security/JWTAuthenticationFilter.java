package com.beeswork.balanceaccountservice.config.security;

import com.beeswork.balanceaccountservice.constant.HttpHeader;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.util.Convert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider         jwtTokenProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JWTAuthenticationFilter(JWTTokenProvider jwtTokenProvider, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) {
        try {
            String token = jwtTokenProvider.resolveAccessToken(httpServletRequest);
            Jws<Claims> jws = jwtTokenProvider.parseJWTToken(token);
            jwtTokenProvider.validateJWTToken(jws);

            String identityTokenInString = httpServletRequest.getHeader(HttpHeader.IDENTITY_TOKEN);
            UUID identityToken = Convert.toUUIDOrThrow(identityTokenInString, new AccountNotFoundException());
            Authentication authentication = jwtTokenProvider.getAuthentication(jws, identityToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
        }
    }
}
