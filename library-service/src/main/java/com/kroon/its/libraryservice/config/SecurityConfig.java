package com.kroon.its.libraryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static final String[] PERMIT_ALL = {
            "/actuator/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/v3/api-docs"
    };

    public static final String[] HAS_ANY_AUTHORITY = {
            "/api/books/**",
            "/api/authors/**",
            "/api/genres/**",
            "/api/book-copies/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(PERMIT_ALL).permitAll()

                        // Loans
                        .requestMatchers(HttpMethod.POST, "/api/loans")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/loans/*/return")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/loans/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Catalog read
                        .requestMatchers(HttpMethod.GET, HAS_ANY_AUTHORITY)
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Catalog create
                        .requestMatchers(HttpMethod.POST, HAS_ANY_AUTHORITY)
                        .hasAuthority("ROLE_ADMIN")

                        // Catalog update
                        .requestMatchers(HttpMethod.PUT, "/api/books/**")
                        .hasAuthority("ROLE_ADMIN")

                        // Catalog delete
                        .requestMatchers(HttpMethod.DELETE, HAS_ANY_AUTHORITY)
                        .hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
