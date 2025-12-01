package it.unicam.cs.ids2425.events.dto;

import it.unicam.cs.ids2425.events.EventType;
import it.unicam.cs.ids2425.location.Location;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventUpdateRequest(
        String title,
        String description,
        EventType type,
        Location location,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        BigDecimal price
) {
}
