package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl extends BaseServiceImpl implements LoginService {



    @Autowired
    public LoginServiceImpl(ModelMapper modelMapper) {
        super(modelMapper);
    }
}
