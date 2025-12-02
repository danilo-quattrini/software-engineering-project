package it.unicam.cs.ids2425.sellerslocations.seeder;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.sellerslocations.service.SLService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.users.roles.seller.Producer;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class SLSeeder implements CommandLineRunner {

    private final SLService slservice;
    private final UserRepository userRepository;

    public SLSeeder(SLService slservice, UserRepository userRepository) {
        this.slservice = slservice;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (!slservice.getAll().isEmpty()) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int sellersToCreate = 10;

        for (int i = 0; i < sellersToCreate; i++) {

            double lat = random.nextDouble(43.0, 44.0);
            double lng = random.nextDouble(12.0, 13.0);

            Location location = new Location (lat, lng, "Address " + (i + 1));

            Producer seller = Producer.builder()
                    .name("Seller " + (i + 1))
                    .email("seller" + i + "@example.com")
                    .password("password")
                    .location(location)
                    .build();

            userRepository.save(seller);
        }

        System.out.println("Seeded " + sellersToCreate + " seller locations.");
    }
}
