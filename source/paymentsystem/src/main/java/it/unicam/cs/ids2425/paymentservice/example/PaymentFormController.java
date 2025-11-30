package it.unicam.cs.ids2425.paymentservice.example;

import it.unicam.cs.ids2425.payment.PaymentDTO;
import it.unicam.cs.ids2425.payment.referable.Referable;
import it.unicam.cs.ids2425.payment.referable.ReferableRepository;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/psp/paymentdemo")
public class PaymentFormController {

    UserRepository userRepository;
    ReferableRepository referableRepository;

    public PaymentFormController(UserRepository userRepository, ReferableRepository referableRepository) {
        this.userRepository = userRepository;
        this.referableRepository = referableRepository;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/payment")
    public String getPaymentDemoPage(
            @RequestParam("referenceId") Long referenceId,
            @AuthenticationPrincipal User user,
            Model model) {

        Referable reference = referableRepository.findById(referenceId)
                .orElseThrow(() -> new RuntimeException("Reference not found"));
        UserDTO payer = userRepository.findByEmail(user.getUsername())
                .map(u -> new UserDTO().toDTO(u))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PaymentDTO dto = new PaymentDTO();
        dto.setReference(reference);
        dto.setPayer(payer);

        boolean isEvent = reference.getClass().getSimpleName().equals("Event");
        model.addAttribute("isEvent", isEvent);
        model.addAttribute("paymentDto", dto);
        return "paymentform";
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/pay")
    public String proceedPayment(
            @ModelAttribute("paymentDto") PaymentDTO dto,
            @RequestParam("psp") String psp,
            Model model) {

        model.addAttribute("paymentDto", dto);
        model.addAttribute("psp", psp);

        return "redirect:/psp/" + psp + "/payment";
    }
}
