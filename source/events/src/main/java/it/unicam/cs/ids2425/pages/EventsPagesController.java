package it.unicam.cs.ids2425.pages;

import it.unicam.cs.ids2425.events.EventState;
import it.unicam.cs.ids2425.events.EventType;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.users.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller("eventsPagesController")
@RequestMapping("/events")
public class EventsPagesController {

    private final UserRepository userRepository;

    public EventsPagesController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public String events(Model model, Authentication authentication) {
        populateCommonAttributes(model, authentication);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("eventStates", EventState.values());
        return "events";
    }

    @PreAuthorize("hasRole('ENTERTAINER')")
    @GetMapping("/create")
    public String createEventPage(Model model, Authentication authentication) {
        populateCommonAttributes(model, authentication);
        model.addAttribute("eventTypes", EventType.values());
        return "create-event";
    }

    private void populateCommonAttributes(Model model, Authentication authentication) {
        Long currentUserId = null;
        String currentUserRole = null;
        String currentUserEmail = null;

        if (authentication != null) {
            Optional<User> optionalUser = userRepository.findByEmail(authentication.getName());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                currentUserId = user.getId();
                currentUserRole = user.getClass().getSimpleName().toUpperCase();
                currentUserEmail = user.getEmail();
            }
        }

        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("currentUserRole", currentUserRole);
        model.addAttribute("currentUserEmail", currentUserEmail);
    }
}
