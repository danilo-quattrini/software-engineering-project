package it.unicam.cs.ids2425.payment.dashboard;

import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.service.PaymentServiceInterface;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/payments/admin")
public class AdminPaymentDashboard {

    private final PaymentServiceInterface paymentService;

    public AdminPaymentDashboard(PaymentServiceInterface paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN')" )
    @RequestMapping("/dashboard")
    public String getProductAdminDashboard(Model model) {

        List<Payment> pendingPayments = paymentService.getPendingPayments();
        model.addAttribute("payments", pendingPayments);

        return "dashboard";
    }

    @PostMapping("/process/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String processPayment(@PathVariable("id") Long id) {
        paymentService.markPaymentProcessed(id);
        return "redirect:/payments/admin/dashboard";
    }
}
