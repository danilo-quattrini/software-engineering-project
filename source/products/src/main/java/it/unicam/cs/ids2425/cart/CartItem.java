package it.unicam.cs.ids2425.cart;

import it.unicam.cs.ids2425.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.text.DecimalFormat;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_quantity")
    private int productQuantity;

    @Column(name = "item_price")
    private double productPrice;

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#0.00");
    /**
     * This means many cart items belong to one cart.
     *
     **/
    @ManyToOne
    @JoinColumn(name = "parent_cart_id", referencedColumnName = "id", nullable = false)
    private Cart parentCart;

    /**
     * This means many cart items can reference the same product.
     *
     **/
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product cartItemProduct;

    public CartItem(Cart parentCart, Product cartItemProduct, int productQuantity, double productPrice) {
        this.parentCart = parentCart;
        this.cartItemProduct = cartItemProduct;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }
    public String getFormattedPrice() {
        return PRICE_FORMAT.format(this.productPrice);
    }
}
