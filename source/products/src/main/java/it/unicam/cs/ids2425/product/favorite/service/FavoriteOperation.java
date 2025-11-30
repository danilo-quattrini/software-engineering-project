package it.unicam.cs.ids2425.product.favorite.service;

import it.unicam.cs.ids2425.product.Product;

import java.util.List;
import java.util.UUID;

public interface FavoriteOperation {
    void addFavorite(Long userId, UUID productId);

    void removeFavorite(Long userId, UUID productId);

    boolean isFavorite(Long userId, UUID productId);

    List<Product> getFavorites(Long userId);
}
