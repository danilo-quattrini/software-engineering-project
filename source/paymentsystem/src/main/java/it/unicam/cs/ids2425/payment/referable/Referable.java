package it.unicam.cs.ids2425.payment.referable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Referable {
    @Id
    @SequenceGenerator(
            name = "referable_sequence",
            sequenceName = "referable_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "referable_sequence"
    )
    private Long id;
    private Double amount;
    private String currencyCode;
}
