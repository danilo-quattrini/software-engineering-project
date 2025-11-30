package it.unicam.cs.ids2425.product.favorite.repository;

import it.unicam.cs.ids2425.product.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndProductId(Long userId, UUID productId);

    Optional<Favorite> findByUserIdAndProductId(Long userId, UUID productId);

    List<Favorite> findAllByUserId(Long userId);
}
