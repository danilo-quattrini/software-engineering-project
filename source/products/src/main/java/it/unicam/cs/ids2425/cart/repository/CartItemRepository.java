package it.unicam.cs.ids2425.cart.repository;

import it.unicam.cs.ids2425.cart.Cart;
import it.unicam.cs.ids2425.cart.CartItem;
import it.unicam.cs.ids2425.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByParentCartAndCartItemProduct(Cart parentCart, Product product);

    CartItem findByCartItemProductId(UUID cartItemProductId);
}
