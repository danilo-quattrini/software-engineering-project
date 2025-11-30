package it.unicam.cs.ids2425.cart.repository;

import it.unicam.cs.ids2425.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
