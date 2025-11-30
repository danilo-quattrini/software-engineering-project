package it.unicam.cs.ids2425.product.bundle.repository;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.product.bundle.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    List<Bundle> findByDistributor(User distributor);
}