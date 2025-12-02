package it.unicam.cs.ids2425.users.controller;


import org.springframework.web.bind.annotation.*;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.service.UserServiceInterface;

import java.util.List;
import java.util.Optional;

@RestController("carloUserController")
@RequestMapping("/users/api")
public class UserController implements UserControllerInterface {

    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping
    @Override
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    @Override
    public Optional<User> get(@PathVariable Long userId) { return userService.get(userId); }

    @GetMapping("/{email}")
    @Override
    public Optional<User> getByEmail(@PathVariable String email) { return userService.getByEmail(email); }

    @PostMapping
    @Override
    public void save(@RequestBody User user) {
        userService.save(user);
    }

    @DeleteMapping(path = "{userId}")
    @Override
    public void delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }

    @PutMapping(path = "{userId}")
    @Override
    public void update(
            @PathVariable("userId") Long userId,
            @ModelAttribute UserDTO dto) {
        userService.update(userId, dto);
    }
}
