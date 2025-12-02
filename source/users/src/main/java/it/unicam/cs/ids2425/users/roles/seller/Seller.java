package it.unicam.cs.ids2425.users.roles.seller;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import it.unicam.cs.ids2425.location.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import it.unicam.cs.ids2425.users.User;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Seller extends User {
    @Embedded
    private Location location;
}
