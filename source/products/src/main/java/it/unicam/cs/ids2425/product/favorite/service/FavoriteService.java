package it.unicam.cs.ids2425.product.favorite.service;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.favorite.Favorite;
import it.unicam.cs.ids2425.product.favorite.repository.FavoriteRepository;
import it.unicam.cs.ids2425.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class    FavoriteService implements FavoriteOperation {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void addFavorite(Long userId, UUID productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return; // skip duplicates
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);

    }

    @Transactional
    @Override
    public void removeFavorite(Long userId, UUID productId) {
        favoriteRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(favoriteRepository::delete);
    }

    @Override
    public boolean isFavorite(Long userId, UUID productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<Product> getFavorites(Long userId) {
        return favoriteRepository.findAllByUserId(userId)
                .stream()
                .map(Favorite::getProduct)
                .toList();
    }
}
