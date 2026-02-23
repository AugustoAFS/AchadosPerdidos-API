package com.AchadosPerdidos.API.Application.Config;

import com.AchadosPerdidos.API.Application.Interfaces.Auth.IJWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

        private final IJWTService jwtService;

        public SecurityConfig(IJWTService jwtService) {
                this.jwtService = jwtService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .httpBasic(basic -> basic.disable())
                                .formLogin(form -> form.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/v1/api-docs/**",
                                                                "/swagger",
                                                                "/api-docs/**",
                                                                "/swagger-resources/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        private OncePerRequestFilter jwtAuthFilter() {
                return new OncePerRequestFilter() {
                        @Override
                        protected void doFilterInternal(
                                        @NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain) throws ServletException, IOException {

                                String authHeader = request.getHeader("Authorization");

                                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                        try {
                                                String token = authHeader.substring(7).trim();

                                                if (jwtService.validateToken(token)) {
                                                        String email = jwtService.getEmailFromToken(token);
                                                        String role = jwtService.getRoleFromToken(token);
                                                        String userId = jwtService.getUserIdFromToken(token);

                                                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                                        email,
                                                                        userId,
                                                                        List.of(new SimpleGrantedAuthority(
                                                                                        "ROLE_" + role)));

                                                        SecurityContextHolder.getContext()
                                                                        .setAuthentication(authentication);
                                                        log.debug("JWT autenticado: userId={}, role={}", userId, role);
                                                }
                                        } catch (Exception e) {
                                                log.warn("Falha ao processar token JWT: {}", e.getMessage());
                                        }
                                }

                                filterChain.doFilter(request, response);
                        }
                };
        }
}
