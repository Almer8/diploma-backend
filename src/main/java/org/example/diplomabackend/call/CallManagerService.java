package org.example.diplomabackend.call;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.call.entities.CallSession;
import org.example.diplomabackend.call.entities.CallStatusResponse;
import org.example.diplomabackend.call.entities.SignalMessage;
import org.example.diplomabackend.visit.entities.VisitEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class CallManagerService {

    private static final Long CALL_BUFFER_MINUTES = 10L;

    private final Map<Long, CallSession> activeCalls = new ConcurrentHashMap<>();
    private final TaskScheduler taskScheduler;
    private final SimpMessagingTemplate messagingTemplate;

    public CallSession createNewCallSession(VisitEntity visit) {

        CallSession newSession = new CallSession(visit.getId());
        Duration duration = Duration.between(visit.getStartTime().toInstant(), visit.getEndTime().toInstant());
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant endTime = localDateTime.toInstant(ZoneId
                .systemDefault()
                .getRules()
                .getOffset(localDateTime)).plus(duration).plus(Duration.ofMinutes(CALL_BUFFER_MINUTES));

        try {
            ScheduledFuture<?> task = taskScheduler.schedule(()-> endCall(visit.getId()), endTime);
            newSession.setTimeoutTask(task);
        } catch (Exception e){
            return null;
        }
        return newSession;


    }

    public void endCall(Long visitId) {
        activeCalls.remove(visitId);
        String topic = "/topic/signal/" + visitId;
        messagingTemplate.convertAndSend(topic, new SignalMessage("call-ended","timeout","system"));
    }


    public CallStatusResponse handleConnectionAttempt(VisitEntity visit, Long userId){
        CallSession session = activeCalls.computeIfAbsent(visit.getId(), id -> createNewCallSession(visit));
        if(session == null){
            throw new RuntimeException("Can't create session for visit " + visit.getId());
        }
        boolean alreadyJoined = session.getParticipants().contains(userId);
        boolean joiningExisting = !session.getParticipants().isEmpty() && !alreadyJoined;

        if(!alreadyJoined){
            session.addParticipant(userId);
        }
        return new CallStatusResponse(visit.getId(), joiningExisting,session.getParticipants().size());

    }
    public Optional<CallSession> getCallSession(Long visitId) {
        return Optional.ofNullable(activeCalls.get(visitId));
    }
    public void removeParticipant(Long visitId, Long userId, String reason) {
        CallSession session = activeCalls.get(visitId);
        if (session != null) {
            boolean removed = session.removeParticipant(userId);
            if (removed) {
                String topic = "/topic/signal/" + visitId;
                messagingTemplate.convertAndSend(topic, new SignalMessage("user-left", userId, "system"));
            }
        }
    }
    @PreDestroy
    public void cleanup() {
        activeCalls.keySet().forEach(this::endCall);
        activeCalls.clear();
    }
}
