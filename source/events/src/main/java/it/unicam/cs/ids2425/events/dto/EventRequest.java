package it.unicam.cs.ids2425.events.dto;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.events.EventType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull EventType type,
        @NotNull Location location,
        @NotNull @FutureOrPresent LocalDateTime startDateTime,
        @NotNull @FutureOrPresent LocalDateTime endDateTime,
        @NotNull @DecimalMin(value = "0.0") Long amount,
        @NotBlank String currencyCode
) {
}

