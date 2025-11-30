package it.unicam.cs.ids2425.users.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.users.roles.Admin;

import java.util.concurrent.ThreadLocalRandom;


@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        // TODO: make lists of parameters to create users

        ThreadLocalRandom random = ThreadLocalRandom.current();
        seedAdminUser();

        // TODO: create and save users using repository.save(...)
    }

    private void seedAdminUser() {
        String adminEmail = "admin@gmail.com";
        String adminPassword = "admin";

        repository.findByEmail(adminEmail).orElseGet(() -> {
            Admin admin = Admin.builder()
                    .name("Platform Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .build();
            return repository.save(admin);
        });
    }
}
