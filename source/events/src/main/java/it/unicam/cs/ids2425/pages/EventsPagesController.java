package it.unicam.cs.ids2425.pages;

import it.unicam.cs.ids2425.events.EventService;
import it.unicam.cs.ids2425.events.EventState;
import it.unicam.cs.ids2425.events.EventType;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.users.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller("eventsPagesController")
@RequestMapping("/events")
public class EventsPagesController {

    private final UserRepository userRepository;
    private final EventService eventService;

    public EventsPagesController(UserRepository userRepository, EventService eventService) {
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public String events(Model model, Authentication authentication, HttpServletRequest request) {
        populateCommonAttributes(model, authentication);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("eventStates", EventState.values());
        String pageurl = request.getRequestURL().toString();
        model.addAttribute("pageurl", pageurl);
        return "events";
    }

    @PreAuthorize("hasRole('ENTERTAINER')")
    @GetMapping("/create")
    public String createEventPage(Model model, Authentication authentication) {
        populateCommonAttributes(model, authentication);
        model.addAttribute("eventTypes", EventType.values());
        return "create-event";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId:\\d+}")
    public String showEvent(@PathVariable("eventId") Long eventId,
                            Model model,
                            Authentication authentication,
                            HttpServletRequest request) {
        populateCommonAttributes(model, authentication);
        model.addAttribute("event", eventService.getEvent(eventId));
        model.addAttribute("pageUrl", request.getRequestURL().toString());
        return "show";
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
