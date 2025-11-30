package it.unicam.cs.ids2425.product.bundle.service;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.product.bundle.Bundle;
import it.unicam.cs.ids2425.product.bundle.repository.BundleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BundleService implements BundleOperation {

    private final BundleRepository bundleRepository;

    public BundleService(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }

    @Override
    public Bundle creaBundle(Bundle bundle, User distributore) {
        bundle.setDistributor(distributore);
        return bundleRepository.save(bundle);
    }

    @Override
    public List<Bundle> getBundleByDistributor(User distributor) {
        return bundleRepository.findByDistributor(distributor);
    }

    @Override
    public Bundle getById(Long id) {
        return bundleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Bundle> getAllBundle() {
        return bundleRepository.findAll();
    }

    @Override
    public void deleteBundle(Long id) {
        Bundle bundle = getById(id);
        bundleRepository.delete(bundle);
    }
}
