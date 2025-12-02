package it.unicam.cs.ids2425.access;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.roles.UserRole;
import it.unicam.cs.ids2425.users.service.UserServiceInterface;

@Controller
@Profile("secured")
public class AccessController {

    private final UserRegistrationFacade userRegistrationFacade;
    private final UserServiceInterface userService;

    public AccessController(UserRegistrationFacade userRegistrationFacade, UserServiceInterface userService) {
        this.userRegistrationFacade = userRegistrationFacade;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", UserRole.values());
        return "access/registrationform";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        userRegistrationFacade.register(userDTO);
        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "email", required = false) String email,
            Model model) {

        UserDTO dto = new UserDTO();
        if (email != null) {
            dto.setEmail(email);
        }

        model.addAttribute("user", dto);
        return "access/loginform";
    }

    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "access/403";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        userService.getByEmail(userDetails.getUsername())
                .ifPresentOrElse(user -> {
                    model.addAttribute("role", user.getSimpleRole());
                    model.addAttribute("name", user.getName());
                    model.addAttribute("email", user.getEmail());
                }, () -> {
                    // redirect o gestione errore
                });
        return "access/profile";
    }

}
