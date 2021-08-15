package com.beeswork.balanceaccountservice.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {
        try {
            String token = jwtTokenProvider.resolveAccessToken(httpServletRequest);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
        }
    }
}
