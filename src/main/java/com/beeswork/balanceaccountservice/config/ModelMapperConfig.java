package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.dto.account.AccountDTO;
import com.beeswork.balanceaccountservice.vm.AccountVM;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

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
        modelMapper.addConverter(stringToUUIDConverter());
        accountVMToAccountDTO(modelMapper);
    }

    private void accountVMToAccountDTO(ModelMapper modelMapper) {
        TypeMap<AccountVM, AccountDTO> typeMap = modelMapper.createTypeMap(AccountVM.class, AccountDTO.class);
        typeMap.addMapping(AccountVM::getAccountQuestionVMs, AccountDTO::setAccountQuestionDTOs);
        typeMap.addMapping(AccountVM::getId, AccountDTO::setId);
    }

    private Converter<String, UUID> stringToUUIDConverter() {
        return new AbstractConverter<>() {
            protected UUID convert(String source) {
                return source == null ? null : UUID.fromString(source);
            }
        };
    }
}
