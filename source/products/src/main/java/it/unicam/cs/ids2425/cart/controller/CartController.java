package it.unicam.cs.ids2425.cart.controller;

import it.unicam.cs.ids2425.cart.Cart;
import it.unicam.cs.ids2425.cart.service.CartOperation;
import it.unicam.cs.ids2425.product.bundle.Bundle;
import it.unicam.cs.ids2425.product.bundle.service.BundleOperation;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.text.DecimalFormat;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartOperation cartOperation;
    private final HttpSession session;
    private static final String SESSION_CART_ID = "cartId";
    private final BundleOperation bundleService;
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
    
    @ModelAttribute("cart")
    public Cart currentCart() {
        User user = getUserAuthenticated();

        if (user != null) {
            Cart userCart = cartOperation.getOrCreateUserCart(user);
            session.setAttribute(SESSION_CART_ID, userCart.getId());
            return userCart;
        }


        Long cartId = getSessionCartId();
        Cart cart = null;

        if (cartId != null) {
            cart = cartOperation.getCart(cartId);
        } else {
            cart = cartOperation.createCart();
        }

        session.setAttribute(SESSION_CART_ID, cart.getId());
        return cart;
    }

    @GetMapping("/items")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String index(Model model, @ModelAttribute("cart") Cart cart) {
        DecimalFormat format = new DecimalFormat("#0.00");
        String price = format.format(cart.getAmount());
        model.addAttribute("cartPrice", price);
        model.addAttribute("cart", cart);
        return "cart/index";
    }

    @PostMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String store(@PathVariable("uuid") UUID uuid) {

        Long cartId = getSessionCartId();

        cartOperation.addProductToCart(cartId, uuid, 1);

        return "redirect:/cart/items";
    }

    @PostMapping("/bundle/{id}")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String storeBundle(@PathVariable("id") Long id) {
        Bundle bundle = bundleService.getById(id);

        Long cartId = getSessionCartId();

        bundle.getProductList().forEach(product -> {
            cartOperation.addProductToCart(cartId, product.getId(), 1); // salva solo UUID
        });

        return "redirect:/cart/items";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String destroy(@PathVariable("id") Long id) {
        cartOperation.removeItem(id);

        return "redirect:/cart/items";
    }

    @PostMapping("/item/{itemId}/increment")
    @PreAuthorize("hasAnyRole('BUYER')")
    @ResponseBody
    public void increment(@PathVariable("itemId") Long itemId) {
        cartOperation.incrementItem(itemId);
    }

    @PostMapping("/item/{itemId}/decrement")
    @PreAuthorize("hasAnyRole('BUYER')")
    @ResponseBody
    public void decrement(@PathVariable("itemId") Long itemId) {
        cartOperation.decrementItem(itemId);
    }

    public Long getSessionCartId() {
        return (Long) session.getAttribute(SESSION_CART_ID);
    }
}