package org.example.diplomabackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;
    @Autowired private VideoCallWebSocketHandler videoCallWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(videoCallWebSocketHandler, "/ws/visit/{visitId}")
                .setAllowedOrigins("*")
                .addInterceptors(jwtHandshakeInterceptor);
    }
}

