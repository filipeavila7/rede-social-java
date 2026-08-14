package com.example.demo.config;

import com.example.demo.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;




    // configuração de hash de senha
    /// Define como senhas são criptografadas e comparadas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ========== POSTS ==========

                        // Meu posts -> precisa estar autenticado
                        .requestMatchers(HttpMethod.GET, "/posts/user/me")
                        .authenticated()

                        // Demais GETs de posts -> públicos
                        .requestMatchers(HttpMethod.GET, "/posts/**")
                        .permitAll()

                        // ========== PÚBLICAS ==========

                        .requestMatchers(
                                "/auth/**",
                                "/uploads/**",
                                "/files/**",
                                "/ws/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/users/new")
                        .permitAll()

                        // Todo o resto
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(authenticationEntryPoint)

                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }
}