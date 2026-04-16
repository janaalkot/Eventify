package com.evently.user_service.config;

import com.evently.user_service.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth

    // PUBLIC
    .requestMatchers("/users/register", "/users/login").permitAll()

        // ADMIN ONLY (inside users service)
        .requestMatchers("/users/admin/**").hasRole("ADMIN")

        // EMPLOYEE + ADMIN
        .requestMatchers("/users/events/manage/**")
        .hasAnyRole("ADMIN", "EMPLOYEE")

        // ALL AUTHENTICATED USERS
        .requestMatchers("/users/events/**")
        .hasAnyRole("USER", "EMPLOYEE", "ADMIN")

        // EVERYTHING ELSE
        .anyRequest().authenticated()
    );

        http.addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}