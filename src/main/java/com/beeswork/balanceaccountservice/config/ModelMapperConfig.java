package com.beeswork.balanceaccountservice.config;


import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.profile.Card;
import net.sf.ehcache.search.parser.MAggregate;
import org.modelmapper.*;
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
        addCardToCardDTOMapping(modelMapper);
    }

    private Converter<String, UUID> stringToUUIDConverter() {
        return new AbstractConverter<>() {
            protected UUID convert(String source) {
                return source == null ? null : UUID.fromString(source);
            }
        };
    }

    private void addCardToCardDTOMapping(ModelMapper modelMapper) {
        TypeMap<Card, CardDTO> typeMap = modelMapper.createTypeMap(Card.class, CardDTO.class);
        Converter<Double, Integer> distanceConverter = c -> c.getSource() <= 0 ? 1 : (int) Math.ceil(c.getSource());
        typeMap.addMappings(mapper -> mapper.using(distanceConverter).map(Card::getDistance, CardDTO::setDistance));
    }
}
