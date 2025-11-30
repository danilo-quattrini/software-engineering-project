package it.unicam.cs.ids2425.sellerslocations.controller;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.sellerslocations.SLDTO;
import it.unicam.cs.ids2425.sellerslocations.service.SLServiceInterface;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import it.unicam.cs.ids2425.users.roles.seller.Seller;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sellerslocations/api")
public class SLController implements SLControllerInterface {
    private final SLServiceInterface slservice;

    public SLController(SLServiceInterface service) { this.slservice = service; }

    @GetMapping
    @Override
    public List<Location> getAll() { return slservice.getAll(); }

    @GetMapping("/sellers")
    @Override
    public List<Seller> getAllSellers() { return slservice.getAllSellers(); }

    @GetMapping("/{sellerId}")
    @Override
    public Optional<Location> get(@PathVariable("sellerId") Long sellerId) { return slservice.get(sellerId); }

    @PreAuthorize("hasAnyRole('PRODUCER', 'DISTRIBUTOR', 'TRANSFORMER')")
    @PostMapping
    @Override
    public void save(@RequestBody Location sellerLocation) { slservice.save(sellerLocation); }

    @PreAuthorize("hasAnyRole('PRODUCER', 'DISTRIBUTOR', 'TRANSFORMER')")
    @DeleteMapping(path = "{sellerId}")
    @Override
    public void delete(@PathVariable("sellerId") Long sellerId) { slservice.delete(sellerId); }

    @PreAuthorize("hasAnyRole('PRODUCER', 'DISTRIBUTOR', 'TRANSFORMER')")
    @PutMapping("/{sellerId}")
    @Override
    public void update(@PathVariable("sellerId") Long sellerId, @RequestBody SLDTO dto) {
        System.out.println("PUT ricevuto per sellerId: " + sellerId + ", dto: " + dto);
        slservice.update(sellerId, dto);
    }
}
