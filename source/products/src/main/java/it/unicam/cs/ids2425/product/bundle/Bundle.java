package it.unicam.cs.ids2425.product.bundle;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bundle")
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name is mandatory.")
    @Size(min = 5, max = 120, message = "The name must be between 5 and 120 characters.")
    private String name;

    @NotNull(message = "The price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    @DecimalMax(value = "999999.99", message = "Price must not exceed 999999.99.")
    private Double price;

    @ManyToMany
    private List<Product> productList;

    @ManyToOne
    private User distributor;

    public Bundle(String name, Double price, List<Product> productList, User distributor) {
        this.name = name;
        this.price = price;
        this.productList = productList;
        this.distributor = distributor;
    }

}