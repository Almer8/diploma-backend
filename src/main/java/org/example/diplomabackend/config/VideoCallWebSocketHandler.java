package org.example.diplomabackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.call.CallManagerService;
import org.example.diplomabackend.call.entities.CallSession;
import org.example.diplomabackend.call.entities.CallStatusResponse;
import org.example.diplomabackend.call.entities.SignalMessage;
import org.example.diplomabackend.visit.VisitService;
import org.example.diplomabackend.visit.entities.VisitEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class VideoCallWebSocketHandler extends TextWebSocketHandler {

    private final CallManagerService callManagerService;
    private final VisitService visitService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long visitId = extractVisitId(session);
        Long userId = extractUserId(session);

        CallSession existingSession = callManagerService.getCallSession(visitId);
        if (existingSession != null && existingSession.getParticipants().contains(userId)) {
            session.close();
            return;
        }

        VisitEntity visit = visitService.getVisit(visitId);
        CallStatusResponse status = callManagerService.handleConnectionAttempt(visit, userId, session);

        SignalMessage joinedMessage = new SignalMessage(
                "joined",
                Map.of("initiator", !status.joiningExisting()),
                "system"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(joinedMessage)));
    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long visitId = extractVisitId(session);
        SignalMessage signal = new ObjectMapper().readValue(message.getPayload(), SignalMessage.class);
        callManagerService.sendSignal(visitId, signal, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long visitId = extractVisitId(session);
        Long userId = extractUserId(session);
        callManagerService.removeParticipant(visitId, userId);
    }

    private Long extractVisitId(WebSocketSession session) {
        return Long.valueOf(session.getUri().getPath().split("/ws/visit/")[1]);
    }

    private Long extractUserId(WebSocketSession session) {
        CustomUserDetails user = (CustomUserDetails) session.getAttributes().get("user");
        return user.getId();
    }
}


