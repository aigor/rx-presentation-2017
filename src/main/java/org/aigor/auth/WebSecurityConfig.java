package org.aigor.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    SecurityWebFilterChain configure(HttpSecurity http) throws Exception {
        return http
//                .authorizeExchange()
//                .pathMatchers("/css/**").permitAll()
//                .pathMatchers("/js/**").permitAll()
//                .pathMatchers("/application/**").permitAll()
//                .pathMatchers("/ws/**").permitAll() // TODO: Use some auth here!
//                .pathMatchers("/**").authenticated().and()
//                .formLogin().loginPage("/login").and()
//                .logout().logoutUrl("/login")
//        .and()
        .build();
    }
}