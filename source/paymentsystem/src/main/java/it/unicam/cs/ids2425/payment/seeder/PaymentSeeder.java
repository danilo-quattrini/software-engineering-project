package it.unicam.cs.ids2425.payment.seeder;

import it.unicam.cs.ids2425.payment.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PaymentSeeder implements CommandLineRunner {

    private final PaymentRepository repository;

    public PaymentSeeder(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO: make lists of parameters to create payments
        // TODO: generate buyers
        // TODO: generate reference ids
        // TODO: generate currency codes
        // TODO: generate amounts
        // TODO: generate addresses
        // TODO: generate locations
        // TODO: generate postal codes
        // TODO: generate country codes

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // TODO: create and save payments using repository.save(...)
    }
}
