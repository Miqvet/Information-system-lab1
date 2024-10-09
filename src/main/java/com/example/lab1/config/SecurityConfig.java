package com.example.lab1.config;

import com.example.lab1.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    // Определяем MD5 PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MessageDigestPasswordEncoder("MD5");
    }

    // Определяем AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**","/images/**", "/js/**","/home","/login","/register").permitAll()               // Доступ для всех к /public/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")            // Доступ только для администраторов к /admin/**
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")  // Доступ для пользователей с ролью USER и ADMIN к /user/**
                        .anyRequest().authenticated()                            // Все остальные запросы требуют аутентификации
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/user", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied") // Страница при ошибке 403 (доступ запрещён)
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}