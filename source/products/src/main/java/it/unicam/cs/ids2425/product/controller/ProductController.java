package it.unicam.cs.ids2425.product.controller;

import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.ProductStatus;
import it.unicam.cs.ids2425.product.favorite.service.FavoriteService;
import it.unicam.cs.ids2425.product.service.ProductOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductOperation productOperation;
    private final UserRepository userRepository;
    private final FavoriteService favoriteService;


    // Method to fetch the authenticated user
    private User getUserAuthenticated() {
        // 1. Take logged-in user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // this is the email

        // 2. Load full User entity from DB
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged user not found"));
    }

    @GetMapping
    public String index(Model model) {
        if (getUserAuthenticated().getSimpleRole().equals("BUYER")) {
            model.addAttribute("products", productOperation.getApprovedProducts());
            model.addAttribute("favoriteIds",
                    favoriteService.getFavorites(getUserAuthenticated().getId())
                            .stream()
                            .map(Product::getId)
                            .toList()
            );
        } else model.addAttribute("products", productOperation.getProductByUser(getUserAuthenticated().getId()));
        return "product/index";
    }

    @GetMapping("/{uuid}")
    public String show(@PathVariable("uuid") UUID uuid, Model model, HttpServletRequest request) {
        Product product = productOperation.showProductInfo(uuid);
        User user = getUserAuthenticated();
        String pageUrl = request.getRequestURL().toString();

        boolean isOwnerOrTrustee = user != null && ("TRUSTEE".equals(user.getSimpleRole())
                || (product.getUser() != null && product.getUser().getId().equals(user.getId())));

        if (product.getStatus() != ProductStatus.APPROVED && !isOwnerOrTrustee) {
            return "redirect:/products";
        }

        model.addAttribute("pageUrl", pageUrl);
        model.addAttribute("product", product);
        return "product/show";
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @GetMapping("/create")
    public String create(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("categories", productOperation.getCategoryList());
        return "product/create";
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("product") Product product,
                        BindingResult result,
                        @RequestParam("file") MultipartFile file,
                        Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("categories", productOperation.getCategoryList());
            return "product/create";
        }

        product.setUser(getUserAuthenticated());
        productOperation.createNewProduct(product, file);

        return "redirect:/products";
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @DeleteMapping("/{uuid}")
    public String delete(@PathVariable("uuid") UUID uuid) {
        productOperation.deleteProduct(uuid);
        return "redirect:/products";
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @GetMapping("/edit/{uuid}")
    public String edit(@PathVariable("uuid") UUID uuid, Model model) {
        Product product = productOperation.showProductInfo(uuid);
        model.addAttribute("product", product);
        model.addAttribute("categories", productOperation.getCategoryList());
        return "product/edit";
    }

    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'PRODUCER', 'TRANSFORMER')")
    @PatchMapping("/edit/{uuid}")
    public String update(@PathVariable("uuid") UUID uuid,
                         @Valid @ModelAttribute("product") Product product,
                         BindingResult result,
                         @RequestParam("file") MultipartFile file,
                         Model model
    ) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("categories", productOperation.getCategoryList());
            return "product/edit";
        }

        productOperation.updateProduct(uuid, product, file);
        return "redirect:/products/" + uuid;
    }

    @PreAuthorize("hasRole('TRUSTEE')")
    @GetMapping("/review")
    public String review(Model model) {
        model.addAttribute("pendingProducts", productOperation.getPendingProducts());
        model.addAttribute("approvedProducts", productOperation.getApprovedProducts());
        return "product/review";
    }

    @PreAuthorize("hasRole('TRUSTEE')")
    @PostMapping("/{uuid}/approve")
    public String approve(@PathVariable("uuid") UUID uuid) {
        productOperation.approveProduct(uuid);
        return "redirect:/products/review";
    }

    @PreAuthorize("hasRole('TRUSTEE')")
    @PostMapping("/{uuid}/reject")
    public String reject(@PathVariable("uuid") UUID uuid) {
        productOperation.rejectProduct(uuid);
        return "redirect:/products/review";
    }
}
