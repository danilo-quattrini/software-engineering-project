package it.unicam.cs.ids2425.product.bundle.service;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.product.bundle.Bundle;

import java.util.List;

public interface BundleOperation {
    Bundle creaBundle(Bundle bundle, User distributore);

    List<Bundle> getBundleByDistributor(User distributor);

    Bundle getById(Long id);

    List<Bundle> getAllBundle();

    void deleteBundle(Long id);
}