package com.beeswork.balanceaccountservice.config.properties;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties("web-mvc")
public class WebMVCProperties {

    private String messageSourcePath;
}
