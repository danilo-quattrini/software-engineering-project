package it.unicam.cs.ids2425.osmcontroller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/osm/")
public class OSMController {

    private final UserRepository userRepository;

    public OSMController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("sellersmap")
    public String sellersmap(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        boolean allowed = List.of("PRODUCER","DISTRIBUTOR","TRANSFORMER").contains(user.getSimpleRole());

        model.addAttribute("canEditSellerLocation", allowed);
        model.addAttribute("sellerId", allowed ? user.getId() : null);
        model.addAttribute("user", user);

        return "/sellersmap";
    }



    @GetMapping("eventsmap")
    public String eventsmap() { return "/eventsmap"; }
}
