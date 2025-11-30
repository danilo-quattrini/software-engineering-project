package it.unicam.cs.ids2425.cart.controller;

import it.unicam.cs.ids2425.payment.service.PaymentService;
import it.unicam.cs.ids2425.cart.Cart;
import it.unicam.cs.ids2425.cart.service.CartOperation;
import it.unicam.cs.ids2425.product.bundle.Bundle;
import it.unicam.cs.ids2425.product.bundle.service.BundleOperation;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartOperation cartOperation;
    private final HttpSession session;

    private static final String SESSION_CART_ID = "cartId";
    private final PaymentService paymentService;
    private final BundleOperation bundleService;

    public CartController(CartOperation cartOperation, HttpSession session, PaymentService paymentService, BundleOperation bundleService) {
        this.cartOperation = cartOperation;
        this.session = session;
        this.paymentService = paymentService;
        this.bundleService = bundleService;
    }

    @ModelAttribute("cart")
    public Cart currentCart() {
        Long cartId = getSessionCartId();

        Cart cart;
        if (cartId != null) {
            cart = cartOperation.getCart(cartId);
        } else {
            cart = cartOperation.createCart();
            session.setAttribute(SESSION_CART_ID, cart.getId());
        }
        return cart;
    }

    @GetMapping("/items")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String index(Model model, @ModelAttribute("cart") Cart cart) {
        if (paymentService.getByReferenceId(cart.getId()).isPresent()) {
            cart = cartOperation.createCart();
            session.setAttribute(SESSION_CART_ID, cart.getId());
        }
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

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('BUYER')")
    public String destroy(@PathVariable("uuid") UUID uuid) {
        cartOperation.removeItem(uuid);

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