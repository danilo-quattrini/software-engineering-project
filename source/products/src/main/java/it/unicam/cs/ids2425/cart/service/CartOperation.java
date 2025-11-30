package it.unicam.cs.ids2425.cart.service;

import it.unicam.cs.ids2425.cart.Cart;

import java.util.UUID;

public interface CartOperation {
    Cart createCart();

    Cart getCart(Long cartId);

    void addProductToCart(Long cartId, UUID productUUID, int quantity);

    void removeItem(Long id);

    void incrementItem(Long itemId);

    void decrementItem(Long itemId);
}
