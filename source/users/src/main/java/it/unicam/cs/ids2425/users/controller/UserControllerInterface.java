package it.unicam.cs.ids2425.users.controller;



import controller.ControllerInterface;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.UserDTO;

import java.util.Optional;

public interface UserControllerInterface extends ControllerInterface<User, UserDTO> {
    public Optional<User> getByEmail(String email);
}
