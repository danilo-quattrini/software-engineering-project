package it.unicam.cs.ids2425.notification;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @PostMapping("/notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable("id") UUID notificationId,
                                         @RequestHeader(value = "referer", required = false) String referer) {
        User user = getAuthenticatedUser();
        if (user != null) {
            notificationService.markAsRead(notificationId, user.getId());
        }
        return "redirect:" + (referer != null ? referer : "/products");
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @PostMapping("/notifications/read-all")
    public String markAllNotificationsAsRead(@RequestHeader(value = "referer", required = false) String referer) {
        User user = getAuthenticatedUser();
        if (user != null) {
            notificationService.markAllAsRead(user.getId());
        }
        return "redirect:" + (referer != null ? referer : "/products");
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }
}