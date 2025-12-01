package it.unicam.cs.ids2425.eventslocations.service;


import it.unicam.cs.ids2425.eventslocations.ELDTO;
import it.unicam.cs.ids2425.eventslocations.repository.ELRepository;
import it.unicam.cs.ids2425.location.Location;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ELService implements ELServiceInterface{
    private final ELRepository elrepository;

    public ELService(ELRepository repository) { this.elrepository = repository; }

    @Override
    public List<Location> getAll() {
        return elrepository.getAllEventLocations();
    }

    @Override
    public Optional<Location> get(Long id) {
        return elrepository.getLocationById(id);
    }

    @Override
    public void save(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Long id, ELDTO dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
