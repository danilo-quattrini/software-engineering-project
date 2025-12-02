package it.unicam.cs.ids2425.product;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.product.certification.Certificate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "The name is mandatory.")
    @Size(min = 5, max = 120, message = "The name must be between 5 and 120 characters.")
    private String name;

    @NotNull(message = "The price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    @DecimalMax(value = "999999.99", message = "Price must not exceed 999999.99.")
    private Double price;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Max(value = 9999, message = "Quantity cannot exceed 9999.")
    private Integer quantity;

    @NotBlank(message = "Description is mandatory.")
    @Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters.")
    private String description;

    @NotNull(message = "Expiration date is required.")
    @Future(message = "Expiration date must be in the future.")
    private LocalDate expireDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Category is required.")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.PENDING;

    @Size(min = 10, max = 255, message = "Production must be between 10 and 255 characters.")
    private String productionPhases;

    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updateAt;

    // Relation within Product and Certificate
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    // Relation within Seller and Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User user;

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#0.00");

    public Product(String name, double price, Integer quantity, String description, LocalDate expireDate, Category category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
        this.expireDate = expireDate;
    }
    public String getFormattedPrice() {
        return PRICE_FORMAT.format(this.price);
    }
}
