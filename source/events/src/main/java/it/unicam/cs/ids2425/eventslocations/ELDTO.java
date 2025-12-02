package it.unicam.cs.ids2425.eventslocations;

import dto.DTOInterface;
import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.events.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ELDTO implements DTOInterface<Event, ELDTO> {
    private Location location;

    @Override
    public ELDTO toDTO(Event entity) {
        return ELDTO.builder()
                .location(entity.getLocation())
                .build();
    }

    @Override
    public Event fromDTO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
