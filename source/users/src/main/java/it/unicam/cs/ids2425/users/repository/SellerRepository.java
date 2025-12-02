package it.unicam.cs.ids2425.users.repository;

import it.unicam.cs.ids2425.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import it.unicam.cs.ids2425.users.roles.seller.Seller;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    @Query("SELECT s.location FROM Seller s WHERE s.id = ?1")
    Optional<Location> findLocationById(Long id);

    @Query("SELECT s.location FROM Seller s")
    List<Location> getAllLocations();
}
