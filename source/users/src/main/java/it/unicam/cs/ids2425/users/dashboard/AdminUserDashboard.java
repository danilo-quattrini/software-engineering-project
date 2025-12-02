package it.unicam.cs.ids2425.users.dashboard;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.service.UserServiceInterface;

import java.util.List;

@Controller
@RequestMapping("/users/admin")
public class AdminUserDashboard {

    private final UserServiceInterface service;

    public AdminUserDashboard(UserServiceInterface service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String getAdminDashboard(Model model) {

        List<User> users = service.getAll();
        model.addAttribute("users", users);

        return "dashboard/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        service.delete(id);
        return "redirect:/users/admin/dashboard";
    }
}
