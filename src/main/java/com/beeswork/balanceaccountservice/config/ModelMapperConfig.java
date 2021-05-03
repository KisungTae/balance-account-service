package com.beeswork.balanceaccountservice.config;


import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import net.sf.ehcache.search.parser.MAggregate;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
        modelMapper.addMappings(matchToDTOMappings());
        return modelMapper;
    }

    private PropertyMap<Match, MatchDTO> matchToDTOMappings() {
        return new PropertyMap<>() {
            @Override protected void configure() {
                skip(destination.getUpdatedAt());
                skip(destination.getActive());
                skip(destination.getDeleted());
                skip(destination.getUnmatched());
            }
        };
    }


    private void setCustomMapping(ModelMapper modelMapper) {
        modelMapper.addConverter(stringToUUIDConverter());
    }

    private Converter<String, UUID> stringToUUIDConverter() {
        return new AbstractConverter<>() {
            protected UUID convert(String source) {
                return source == null ? null : UUID.fromString(source);
            }
        };
    }
}
