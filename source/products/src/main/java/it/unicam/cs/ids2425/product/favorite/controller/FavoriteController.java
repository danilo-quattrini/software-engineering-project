package it.unicam.cs.ids2425.product.favorite.controller;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.product.favorite.service.FavoriteOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteOperation favoriteOperation;
    private final UserRepository userRepository;

    // Method to fetch the authenticated user
    private User getUserAuthenticated() {
        // 1. Take logged-in user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // this is the email

        // 2. Load full User entity from DB
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged user not found"));
    }

    @PostMapping("/add/{uuid}")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String addFavorite(@PathVariable("uuid") UUID productId) {
        favoriteOperation.addFavorite(getUserAuthenticated().getId(), productId);
        return "redirect:/favorites";
    }

    @PostMapping("/remove/{uuid}")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String removeFavorite(@PathVariable("uuid") UUID productId) {

        if (!getUserAuthenticated().getSimpleRole().equals("BUYER")) {
            return "redirect:/access-denied";
        }

        favoriteOperation.removeFavorite(getUserAuthenticated().getId(), productId);
        return "redirect:/favorites";
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUYER')")
    public String getFavorites(Model model) {

        model.addAttribute("favorites", favoriteOperation.getFavorites(getUserAuthenticated().getId()));
        return "favorites/list";
    }
}
