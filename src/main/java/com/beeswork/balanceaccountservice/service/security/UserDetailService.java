package com.beeswork.balanceaccountservice.service.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserDetailService {
    UserDetails loadUserByUsername(UUID userName, UUID identityToken);
}
