package it.unicam.cs.ids2425.product.bundle.controller;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.product.bundle.Bundle;
import it.unicam.cs.ids2425.product.bundle.service.BundleOperation;
import it.unicam.cs.ids2425.product.service.ProductOperation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bundle")
public class BundleController {

    private final BundleOperation bundleOperation;
    private final ProductOperation productOperation;
    private final UserRepository userRepository;

    public BundleController(BundleOperation bundleOperation, ProductOperation productOperation, UserRepository userRepository) {
        this.bundleOperation = bundleOperation;
        this.productOperation = productOperation;
        this.userRepository = userRepository;
    }

    // Method to fetch the authenticated user
    private User getUserAuthenticated() {
        // 1. Take logged-in user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // this is the email

        // 2. Load full User entity from DB
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged user not found"));
    }


    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR')")
    public String creaBundleForm(Model model) {
        model.addAttribute("bundle", new Bundle());
        model.addAttribute("products", productOperation.getProductByUser(getUserAuthenticated().getId()));
        return "bundle/index";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR')")
    public String creaBundle(@Valid @ModelAttribute("bundle") Bundle bundle,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("products", productOperation.getProductByUser(getUserAuthenticated().getId()));
            return "bundle/index";
        }
        if (bundle.getProductList() == null || bundle.getProductList().size() < 2) {
            model.addAttribute("products", productOperation.getProductByUser(getUserAuthenticated().getId()));
            model.addAttribute("productError", "Il bundle deve contenere almeno due prodotti.");
            return "bundle/index";
        }
        bundleOperation.creaBundle(bundle, getUserAuthenticated());
        return "redirect:/bundle/dashboard";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR')")
    public String delete(@PathVariable("id") Long id) {
        bundleOperation.deleteBundle(id);
        return "redirect:/bundle/dashboard";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR')")
    public String dashboard(Model model) {
        model.addAttribute("bundle", bundleOperation.getBundleByDistributor(getUserAuthenticated()));
        return "bundle/list";
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'BUYER')")
    public String index(Model model)
    {
        model.addAttribute("bundle", bundleOperation.getAllBundle());
        return "bundle/list";
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'BUYER')")
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("bundle", bundleOperation.getById(id));
        model.addAttribute("products", bundleOperation.getById(id).getProductList());
        return "bundle/show";
    }
}