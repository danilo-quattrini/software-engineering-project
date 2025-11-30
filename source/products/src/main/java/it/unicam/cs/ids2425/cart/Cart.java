package it.unicam.cs.ids2425.cart;

import it.unicam.cs.ids2425.payment.referable.Referable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart")
public class Cart extends Referable {

    @OneToMany(mappedBy = "parentCart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    public void addItem(CartItem item) {
        cartItems.add(item);
        item.setParentCart(this);
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
        item.setParentCart(null);
        updateTotal();
    }

    public void updateTotal() {
        setAmount(Math.floor(cartItems.stream()
                .mapToDouble(item -> item.getProductQuantity() * item.getCartItemProduct().getPrice())
                .sum()));
    }


    public Cart(double amount) {
        setAmount(amount);
    }
}
