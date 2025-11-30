package it.unicam.cs.ids2425.users.roles.seller;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
public class Transformer extends Seller { }
