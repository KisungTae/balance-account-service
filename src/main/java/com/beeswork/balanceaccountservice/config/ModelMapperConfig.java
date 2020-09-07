package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.dto.AccountDTO;
import com.beeswork.balanceaccountservice.entity.Account;
import com.beeswork.balanceaccountservice.vm.AccountVM;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ModelMapperConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        setCustomMapping(modelMapper);
        return modelMapper;
    }

    private void setCustomMapping(ModelMapper modelMapper) {
        accountVMToAccountDTO(modelMapper);
    }

    private void accountVMToAccountDTO(ModelMapper modelMapper) {
        TypeMap<AccountVM, AccountDTO> typeMap = modelMapper.createTypeMap(AccountVM.class, AccountDTO.class);
        typeMap.addMapping(AccountVM::getAccountQuestionVMs, AccountDTO::setAccountQuestionVMs);
    }
}
