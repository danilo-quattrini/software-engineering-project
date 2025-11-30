package it.unicam.cs.ids2425.paymentservice.provider.controller;

import it.unicam.cs.ids2425.payment.PaymentDTO;
import it.unicam.cs.ids2425.paymentservice.provider.PaymentServiceProvider;
import it.unicam.cs.ids2425.paymentservice.provider.factory.PaymentServiceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/psp")
public class PaymentServiceProviderController {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceProviderController.class);

    private final PaymentServiceProviderFactory factory;

    public PaymentServiceProviderController(PaymentServiceProviderFactory factory) {
        this.factory = factory;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/{psp}")
    public String getPaymentServiceProvider(@PathVariable("psp") String psp, Model model) {
        PaymentServiceProvider provider = factory.getProvider(psp);
        if (provider == null) {
            return "redirect:/pspnotfound"; // fallback page
        }

        model.addAttribute("psp", psp);
        model.addAttribute("paymentDTO", new PaymentDTO());
        return "it/unicam/cs/carlo/psp/providers/" + psp; // loads PSP's HTML file inside the popup
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/{psp}")
    public String processPayment(@ModelAttribute PaymentDTO dto,
                                 @PathVariable("psp") String psp,
                                 RedirectAttributes redirectAttributes) {
        PaymentServiceProvider provider = factory.getProvider(psp);
        if (provider == null) {
            redirectAttributes.addFlashAttribute("error", "PSP not valid");
            return "redirect:/error";
        }

        boolean success = provider.processPayment(dto);
        redirectAttributes.addFlashAttribute("success", success);

        return "redirect:/order/confirmed";
    }
}
