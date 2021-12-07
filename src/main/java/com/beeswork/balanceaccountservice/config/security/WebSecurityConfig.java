package com.beeswork.balanceaccountservice.config.security;

import com.beeswork.balanceaccountservice.config.properties.GoogleLoginProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final GoogleLoginProperties        googleLoginProperties;
    private final JWTAuthenticationFilter      jwtAuthenticationFilter;
    private final RestAccessDeniedHandler      restAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    public WebSecurityConfig(GoogleLoginProperties googleLoginProperties,
                             JWTAuthenticationFilter jwtAuthenticationFilter,
                             RestAccessDeniedHandler restAccessDeniedHandler,
                             RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.googleLoginProperties = googleLoginProperties;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
//      TODO: remove /dummy/** and question/random/list
//      TODO: comment in

        http.httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/login/**", "/dummy/**", "/web-socket").permitAll()
            .antMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .accessDeniedHandler(restAccessDeniedHandler)
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleLoginProperties.getClientId())).build();
    }
}

