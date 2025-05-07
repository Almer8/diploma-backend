package org.example.diplomabackend.call;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.call.entities.CallSession;
import org.example.diplomabackend.call.entities.CallStatusResponse;
import org.example.diplomabackend.call.entities.SignalMessage;
import org.example.diplomabackend.visit.entities.VisitEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class CallManagerService {

    private static final Long CALL_BUFFER_MINUTES = 10L;
    private final Map<Long, CallSession> activeCalls = new ConcurrentHashMap<>();
    private final TaskScheduler taskScheduler;

    public CallSession createNewCallSession(VisitEntity visit) {
        CallSession newSession = new CallSession(visit.getId());

        Duration duration = Duration.between(
                visit.getStartTime().toInstant(),
                visit.getEndTime().toInstant()
        );

        Instant endTime = Instant.now().plus(duration).plus(Duration.ofMinutes(CALL_BUFFER_MINUTES));

        ScheduledFuture<?> task = taskScheduler.schedule(() -> endCall(visit.getId()), endTime);
        newSession.setTimeoutTask(task);
        activeCalls.put(visit.getId(), newSession);

        return newSession;
    }

    public void endCall(Long visitId) {
        activeCalls.remove(visitId);
    }

    public CallStatusResponse handleConnectionAttempt(VisitEntity visit, Long userId, WebSocketSession session) {
        CallSession sessionForVisit = activeCalls.computeIfAbsent(
                visit.getId(),
                id -> {
                    CallSession newSession = new CallSession(visit.getId());
                    Duration duration = Duration.between(visit.getStartTime().toInstant(), visit.getEndTime().toInstant());
                    Instant endTime = Instant.now().plus(duration).plus(Duration.ofMinutes(CALL_BUFFER_MINUTES));
                    ScheduledFuture<?> task = taskScheduler.schedule(() -> endCall(visit.getId()), endTime);
                    newSession.setTimeoutTask(task);
                    return newSession;
                }
        );

        if (sessionForVisit.getParticipants().contains(userId)) {
            return new CallStatusResponse(visit.getId(), false, sessionForVisit.getParticipants().size(), sessionForVisit);
        }

        boolean joiningExisting = !sessionForVisit.getParticipants().isEmpty();
        sessionForVisit.addParticipant(userId, session);
        if (joiningExisting && sessionForVisit.getPendingOffer() != null) {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(sessionForVisit.getPendingOffer())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new CallStatusResponse(visit.getId(), joiningExisting, sessionForVisit.getParticipants().size(), sessionForVisit);
    }
    public CallSession getCallSession(Long visitId) {
        return activeCalls.get(visitId);
    }


    public void removeParticipant(Long visitId, Long userId) {
        CallSession session = activeCalls.get(visitId);
        if (session != null && session.removeParticipant(userId) && session.isEmpty()) {
            endCall(visitId);
        }
    }

    public void sendSignal(Long visitId, SignalMessage message, WebSocketSession senderSession) throws IOException {
        CallSession session = activeCalls.get(visitId);
        if (session == null) return;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(message);

        List<WebSocketSession> recipients = session.getAllSessionsExcept(senderSession);

        if (recipients.isEmpty() && message.getType().equals("offer")) {
            session.storePendingOffer(message);
            return;
        }

        for (WebSocketSession recipient : recipients) {
            if (recipient.isOpen()) {
                recipient.sendMessage(new TextMessage(json));
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        activeCalls.keySet().forEach(this::endCall);
    }
}


