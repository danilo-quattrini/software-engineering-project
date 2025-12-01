package it.unicam.cs.ids2425.events.dto;

import it.unicam.cs.ids2425.events.EventState;
import jakarta.validation.constraints.NotNull;

public record EventStateUpdateRequest(@NotNull EventState state) {
}
