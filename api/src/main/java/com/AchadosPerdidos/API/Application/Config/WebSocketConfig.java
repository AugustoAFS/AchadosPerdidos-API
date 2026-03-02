package com.AchadosPerdidos.API.Application.Config;

import com.AchadosPerdidos.API.Application.Interfaces.Auth.IJWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private IJWTService jwtService;

    /**
     * Scheduler necessário para o SimpleBroker executar os ticks de heartbeat.
     * Sem ele, setHeartbeatValue lança IllegalArgumentException na inicialização.
     */
    @Bean
    public ThreadPoolTaskScheduler webSocketTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[] { 25000, 25000 })
                .setTaskScheduler(webSocketTaskScheduler()); // obrigatório com heartbeat
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // fallback para clientes sem suporte nativo a WebSocket
    }

    /**
     * Intercepta a fase CONNECT do STOMP para autenticar o JWT.
     * O cliente Flutter deve enviar o header: "Authorization: Bearer <token>"
     * no momento da conexão WebSocket.
     */
    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            @NonNull
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7).trim();
                        try {
                            if (jwtService.validateToken(token)) {
                                String email = jwtService.getEmailFromToken(token);
                                String role = jwtService.getRoleFromToken(token);
                                String userId = jwtService.getUserIdFromToken(token);

                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        email,
                                        userId,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                                accessor.setUser(auth);
                                log.info("WS CONNECT autenticado: userId={}, email={}", userId, email);
                            } else {
                                log.warn("WS CONNECT rejeitado: token inválido");
                            }
                        } catch (Exception e) {
                            log.warn("WS CONNECT falhou ao validar JWT: {}", e.getMessage());
                        }
                    } else {
                        log.warn("WS CONNECT sem header Authorization");
                    }
                }
                return message;
            }
        });
    }
}