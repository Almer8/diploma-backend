package org.example.diplomabackend.call.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Data
@NoArgsConstructor
public class CallSession {

    private Long visitId;
    private Set<Long> participants = new HashSet<>();
    private LocalDateTime callStartTime;
    private ScheduledFuture<?> timeoutTask;

    public CallSession(Long visitId){
        this.visitId = visitId;
        this.callStartTime = LocalDateTime.now();
    }

    public void addParticipant(Long participantId) {
        this.participants.add(participantId);
    }

    public boolean removeParticipant(Long participantId) {
        return this.participants.remove(participantId);
    }

    public boolean isEmpty() {
        return this.participants.isEmpty();
    }
}
