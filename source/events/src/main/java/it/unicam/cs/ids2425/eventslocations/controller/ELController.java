package it.unicam.cs.ids2425.eventslocations.controller;

import it.unicam.cs.ids2425.eventslocations.ELDTO;
import it.unicam.cs.ids2425.eventslocations.service.ELService;
import it.unicam.cs.ids2425.location.Location;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventslocations")
public class ELController implements ELControllerInterface{
    private final ELService elservice;

    public ELController(ELService service) { this.elservice = service; }

    @GetMapping
    @Override
    public List<Location> getAll() {
        return elservice.getAll();
    }

    @GetMapping("{id}")
    @Override
    public Optional<Location> get(@PathVariable("id") Long id) {
        return elservice.get(id);
    }

    @PostMapping
    @Override
    public void save(Location location) {
        elservice.save(location);
    }

    @DeleteMapping("{id}")
    @Override
    public void delete(@PathVariable("id") Long id) {
        elservice.delete(id);
    }

    @PutMapping("{id}")
    @Override
    public void update(@PathVariable("id") Long id, @ModelAttribute ELDTO dto) {
        elservice.update(id, dto);
    }
}
