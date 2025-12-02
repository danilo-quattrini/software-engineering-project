package it.unicam.cs.ids2425.users.roles;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import it.unicam.cs.ids2425.users.User;

@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
public class Buyer extends User { }
