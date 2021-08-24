package com.beeswork.balanceaccountservice.service.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailService {
    UserDetails loadUserByUsername(String userName, String identityToken);
}
