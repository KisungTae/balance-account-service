package com.beeswork.balanceaccountservice.service.base;

import org.modelmapper.ModelMapper;

public class BaseServiceImpl {

    protected final ModelMapper modelMapper;

    public BaseServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
