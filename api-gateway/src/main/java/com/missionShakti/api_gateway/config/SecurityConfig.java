package com.missionShakti.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange

                        .pathMatchers("/actuator/**").permitAll()


                        .pathMatchers("/api/auth/**").permitAll()


                        .pathMatchers(HttpMethod.POST, "/api/users/**")
                        .hasRole("SUPER_ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/users/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")


                        .anyExchange().authenticated()
                )
                .build();
    }
}
