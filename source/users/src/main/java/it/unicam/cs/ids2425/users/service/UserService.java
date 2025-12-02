package it.unicam.cs.ids2425.users.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> get(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getByEmail(String email) { return userRepository.findByEmail(email); }

    @Override
    public void save(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        if(optionalUser.isPresent()) {
            throw new IllegalStateException("email taken");
        }

        userRepository.save(user);
    }

    @Override
    public void delete(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if(!exists) {
            throw new IllegalStateException("user with id " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public void update(Long userId, UserDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("user with id " + userId + " does not exist"));
        boolean updated = false;
        updated |= updateField(dto.getName(), user.getName(), user::setName);
        updated |= updateField(dto.getEmail(), user.getEmail(), user::setEmail);
        updated |= updateField(dto.getPassword(), user.getPassword(), user::setPassword);
        if (updated) userRepository.save(user);
    }
}
