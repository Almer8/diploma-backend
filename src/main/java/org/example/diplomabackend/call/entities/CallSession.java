package org.example.diplomabackend.call.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CallSession {

    private Long visitId;
    private Set<Long> participants = new HashSet<>();
    private Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private LocalDateTime callStartTime;
    private ScheduledFuture<?> timeoutTask;
    @Getter
    private SignalMessage pendingOffer;

    public CallSession(Long visitId){
        this.visitId = visitId;
        this.callStartTime = LocalDateTime.now();
    }

    public void storePendingOffer(SignalMessage offer) {
        this.pendingOffer = offer;
    }
    public SignalMessage getPendingOfferAndClear() {
        SignalMessage tmp = this.pendingOffer;
        this.pendingOffer = null;
        return tmp;
    }

    public void addParticipant(Long participantId, WebSocketSession session) {
        this.participants.add(participantId);
        this.sessions.add(session);

    }

    public boolean removeParticipant(Long participantId) {
        return participants.remove(participantId);
    }

    public List<WebSocketSession> getAllSessionsExcept(WebSocketSession excluded) {
        return this.sessions.stream()
                .filter(s -> !s.getId().equals(excluded.getId()))
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return participants.isEmpty();
    }
}


