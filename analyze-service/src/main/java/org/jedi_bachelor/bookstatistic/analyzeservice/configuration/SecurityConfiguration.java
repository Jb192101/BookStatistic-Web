package org.jedi_bachelor.bookstatistic.analyzeservice.configuration;

import org.jedi_bachelor.bookstatistic.analyzeservice.utils.JwtAuthenticationFilter;
import org.jedi_bachelor.bookstatistic.analyzeservice.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfiguration {
    private final JwtUtil jwtUtil;

    public SecurityConfiguration(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        //.requestMatchers("/auth/login").permitAll()
                        //.requestMatchers("/users/login/**").permitAll()
                        //.requestMatchers("/products/**").hasAnyRole("MASTER", "GRAND_EMPLOYEE")
                        //.requestMatchers("/client-products/**").hasAnyRole("MASTER", "GRAND_EMPLOYEE", "CURRENT_CLIENT")
                        //.requestMatchers("/internal/**").hasAnyRole("MASTER", "GRAND_EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(this.jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
