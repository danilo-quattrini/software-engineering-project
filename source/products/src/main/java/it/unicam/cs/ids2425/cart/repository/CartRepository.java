package it.unicam.cs.ids2425.cart.repository;

import it.unicam.cs.ids2425.cart.Cart;
import it.unicam.cs.ids2425.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByOwner(User owner);
}
