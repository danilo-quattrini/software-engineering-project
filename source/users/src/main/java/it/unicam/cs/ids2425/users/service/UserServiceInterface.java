package it.unicam.cs.ids2425.users.service;

import service.ServiceInterface;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.UserDTO;

import java.util.Optional;

public interface UserServiceInterface extends ServiceInterface<User, UserDTO> {
    public Optional<User> getByEmail(String email);
}
