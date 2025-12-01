package it.unicam.cs.ids2425.eventslocations.repository;

import it.unicam.cs.ids2425.events.Event;
import it.unicam.cs.ids2425.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ELRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e.location FROM Event e")
    List<Location> getAllEventLocations();

    @Query("SELECT e.location FROM Event e WHERE e.id = ?1")
    Optional<Location> getLocationById(Long id);
}
