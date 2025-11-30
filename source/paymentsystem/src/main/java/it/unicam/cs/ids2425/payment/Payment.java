package it.unicam.cs.ids2425.payment;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.payment.referable.Referable;
import it.unicam.cs.ids2425.users.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @SequenceGenerator(
            name = "payment_sequence",
            sequenceName = "payment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payment_sequence"
    )
    private Long id;
    @ManyToOne
    private User payer;
    @ManyToOne
    private Referable reference;
    @Embedded
    private Location location;
    private boolean processed;
}
