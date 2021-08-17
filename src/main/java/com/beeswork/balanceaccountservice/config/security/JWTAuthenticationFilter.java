package com.beeswork.balanceaccountservice.config.security;

import com.beeswork.balanceaccountservice.constant.HttpHeader;
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
            if (token != null && jwtTokenProvider.validateAccessToken(token)) {
                String identityToken = httpServletRequest.getHeader(HttpHeader.IDENTITY_TOKEN);
                Authentication authentication = jwtTokenProvider.getAuthentication(token, identityToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
        }
    }
}
