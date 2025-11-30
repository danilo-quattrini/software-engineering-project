package it.unicam.cs.ids2425.product.certification;

import it.unicam.cs.ids2425.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificate")
public class Certificate {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "certificate_sequence"
    )
    @SequenceGenerator(
            sequenceName = "certificate_sequence",
            name = "certificate_sequence",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "data")
    private byte[] data;

    @OneToOne(mappedBy = "certificate")
    private Product product;

}
