package it.unicam.cs.ids2425.sellerslocations.service;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.sellerslocations.SLDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import it.unicam.cs.ids2425.users.repository.SellerRepository;
import it.unicam.cs.ids2425.users.roles.seller.Seller;

import java.util.List;
import java.util.Optional;

@Service
public class SLService implements SLServiceInterface {
    private final SellerRepository sellerrepository;

    public SLService(SellerRepository sellerrepository) { this.sellerrepository = sellerrepository; }

    @Override
    public List<Location> getAll() { return sellerrepository.getAllLocations(); }

    @Override
    public List<Seller> getAllSellers() { return sellerrepository.findAll(); }

    @Override
    public Optional<Location> get(Long id) { return sellerrepository.findLocationById(id); }

    @Override
    public void save(Location sellerLocation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {

    }

    @Transactional
    @Override
    public void update(Long sellerId, SLDTO dto) {
        Seller seller = sellerrepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        updateField(dto.getLocation().fromDTO(), seller.getLocation(), seller::setLocation);
        sellerrepository.save(seller);
    }
}
