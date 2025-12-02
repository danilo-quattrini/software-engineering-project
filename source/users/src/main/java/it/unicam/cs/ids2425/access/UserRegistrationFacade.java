package it.unicam.cs.ids2425.access;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.service.UserService;

@Component
public class UserRegistrationFacade {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationFacade(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userService.save(userDTO.fromDTO());
    }
}
