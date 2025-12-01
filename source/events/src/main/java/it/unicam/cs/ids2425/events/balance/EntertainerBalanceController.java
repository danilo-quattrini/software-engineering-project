package it.unicam.cs.ids2425.events.balance;


import it.unicam.cs.ids2425.events.Event;
import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.referable.Referable;
import it.unicam.cs.ids2425.payment.service.PaymentServiceInterface;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.service.UserServiceInterface;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/events/entertainer")
public class EntertainerBalanceController {
    private final UserServiceInterface userService;
    private final PaymentServiceInterface paymentService;

    public EntertainerBalanceController(UserServiceInterface userService, PaymentServiceInterface paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/balance")
    public String getEntertainerBalance(Model model, User principal) {

        User entertainer = userService.getByEmail(principal.getEmail()).orElseThrow();
        double balance = getEntertainerBalance(entertainer);

        model.addAttribute("balance", balance);

        return "entertainerbalance";
    }


    private double getEntertainerBalance(User entertainer) {

        List<Payment> payments = paymentService.getProcessedPayments();

        return payments.stream()
                .map(Payment::getReference)
                .filter(ref -> ref instanceof Event)
                .map(ref -> (Event) ref)
                .filter(event -> event.getEntertainer().equals(entertainer))
                .mapToDouble(Referable::getAmount)
                .sum();
    }
}
