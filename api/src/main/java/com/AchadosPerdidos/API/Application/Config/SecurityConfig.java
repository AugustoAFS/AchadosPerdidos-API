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

import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

        private final IJWTService jwtService;
        private final CorsConfigurationSource corsConfigurationSource;

        public SecurityConfig(IJWTService jwtService, CorsConfigurationSource corsConfigurationSource) {
                this.jwtService = jwtService;
                this.corsConfigurationSource = corsConfigurationSource;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(csrf -> csrf.disable())
                                .httpBasic(basic -> basic.disable())
                                .formLogin(form -> form.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        log.warn("Requisição não autenticada: {} {}",
                                                                        request.getMethod(), request.getRequestURI());
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\":\"Não autenticado\",\"message\":\""
                                                                                        + authException.getMessage()
                                                                                        + "\"}");
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        log.warn("Acesso negado: {} {} - {}",
                                                                        request.getMethod(), request.getRequestURI(),
                                                                        accessDeniedException.getMessage());
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\":\"Acesso negado\",\"message\":\""
                                                                                        + accessDeniedException
                                                                                                        .getMessage()
                                                                                        + "\"}");
                                                }))
                                .authorizeHttpRequests(auth -> auth
                                                // ── Preflight CORS ──
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // ── Raiz e Health ──
                                                .requestMatchers("/").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/error").permitAll()

                                                // ── Swagger / OpenAPI ──
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/v1/api-docs/**",
                                                                "/swagger",
                                                                "/api-docs/**",
                                                                "/swagger-resources/**",
                                                                "/webjars/**")
                                                .permitAll()

                                                // ── WebSocket (autenticação feita via JWT no STOMP CONNECT) ──
                                                .requestMatchers("/ws/**").permitAll()

                                                // ── Auth (público) ──
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // ── Users ──
                                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()

                                                // ── Items (GET público, escrita autenticada) ──
                                                .requestMatchers(HttpMethod.GET, "/api/items/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/items").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/items/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/items/**").authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/api/items/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/items/**").authenticated()

                                                // ── Categories (GET público, escrita autenticada) ──
                                                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/categories/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/categories/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                                                .authenticated()

                                                // ── Campuses (GET público, escrita autenticada) ──
                                                .requestMatchers(HttpMethod.GET, "/api/campuses/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/campuses").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/campuses/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/campuses/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/campuses/**").authenticated()

                                                // ── Institutions (GET público, escrita autenticada) ──
                                                .requestMatchers(HttpMethod.GET, "/api/institutions/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/institutions").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/institutions/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/institutions/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/institutions/**")
                                                .authenticated()

                                                // ── Photos (GET público, escrita autenticada) ──
                                                .requestMatchers(HttpMethod.GET, "/api/photos/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/photos/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/photos/**").authenticated()

                                                // ── Chats (tudo autenticado) ──
                                                .requestMatchers("/api/chats/**").authenticated()

                                                // ── Fallback: qualquer outra rota exige autenticação ──
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

                                String path = request.getRequestURI();
                                String method = request.getMethod();
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
                                                        log.info("JWT autenticado: {} {} - userId={}, email={}, role={}",
                                                                        method, path, userId, email, role);
                                                } else {
                                                        log.warn("Token JWT inválido: {} {}", method, path);
                                                }
                                        } catch (Exception e) {
                                                log.warn("Falha ao processar token JWT em {} {}: {}",
                                                                method, path, e.getMessage());
                                        }
                                } else {
                                        log.debug("Sem header Authorization: {} {}", method, path);
                                }

                                filterChain.doFilter(request, response);
                        }
                };
        }
}
