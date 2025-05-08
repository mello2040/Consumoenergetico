package br.com.fiap.consumoenergetico.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desabilita CSRF para testes com POST/PUT sem token
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // libera seus endpoints da API
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
