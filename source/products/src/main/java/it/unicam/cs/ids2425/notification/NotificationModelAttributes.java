package it.unicam.cs.ids2425.notification;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class NotificationModelAttributes {

    private static final Set<String> SELLER_ROLES = Set.of("PRODUCER", "TRANSFORMER", "DISTRIBUTOR");

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationModelAttributes(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @ModelAttribute("notifications")
    public List<Notification> notifications() {
        User authenticated = getAuthenticatedUser();
        if (!isSeller(authenticated)) {
            return Collections.emptyList();
        }
        return notificationService.getNotificationsForUser(authenticated.getId());
    }

    @ModelAttribute("unreadNotificationsCount")
    public long unreadNotificationsCount() {
        User authenticated = getAuthenticatedUser();
        if (!isSeller(authenticated)) {
            return 0;
        }
        return notificationService.countUnreadNotifications(authenticated.getId());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    private boolean isSeller(User user) {
        if (user == null) {
            return false;
        }
        return SELLER_ROLES.contains(user.getSimpleRole());
    }
}