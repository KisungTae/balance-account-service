package com.beeswork.balanceaccountservice.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    private final HandlerExceptionResolver handlerExceptionResolver;
//
//    @Autowired
//    public CustomAuthenticationEntryPoint(HandlerExceptionResolver handlerExceptionResolver) {
//        this.handlerExceptionResolver = handlerExceptionResolver;
//    }
//
//    @Override
//    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
//    throws IOException, ServletException {
//        System.out.println("handlerExceptionResolver.resolveException");
//        System.out.println(e.getLocalizedMessage());
//        handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
//    }
//}
