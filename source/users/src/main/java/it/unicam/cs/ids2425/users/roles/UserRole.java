package it.unicam.cs.ids2425.users.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.roles.seller.Distributor;
import it.unicam.cs.ids2425.users.roles.seller.Producer;
import it.unicam.cs.ids2425.users.roles.seller.Transformer;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
public enum UserRole {
    BUYER("buyer", Buyer::builder),
    PRODUCER("producer", Producer::builder),
    TRANSFORMER("transformer", Transformer::builder),
    DISTRIBUTOR("distributor", Distributor::builder),
    ENTERTAINER("entertainer", Entertainer::builder),
    TRUSTEE("trustee", Trustee::builder);

    private final String role;
    private final Supplier<? extends User.UserBuilder<?, ?>> builder;
}
