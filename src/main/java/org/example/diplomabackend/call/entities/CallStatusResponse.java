package org.example.diplomabackend.call.entities;

public record CallStatusResponse(
        Long visitId,
        Boolean joiningExisting,
        Integer currentParticipants
) {}
