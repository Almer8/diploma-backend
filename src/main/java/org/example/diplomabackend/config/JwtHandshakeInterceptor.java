package org.example.diplomabackend.config;

import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.auth.security.CustomUserDetailsService;
import org.example.diplomabackend.auth.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired private JwtService jwtService;
    @Autowired private CustomUserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter("token");


        String email = jwtService.extractSubject(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (token == null || !jwtService.isTokenValid(token,userDetails)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }


        attributes.put("user", userDetails);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}


