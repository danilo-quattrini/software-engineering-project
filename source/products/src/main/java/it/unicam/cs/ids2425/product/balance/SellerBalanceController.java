package it.unicam.cs.ids2425.product.balance;

import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.service.PaymentServiceInterface;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.service.UserServiceInterface;
import it.unicam.cs.ids2425.cart.Cart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products/seller")
public class SellerBalanceController {
    private final UserServiceInterface userService;
    private final PaymentServiceInterface paymentService;

    public SellerBalanceController(UserServiceInterface userService, PaymentServiceInterface paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/balance")
    public String getSellerBalance(Model model, User principal) {

        User seller = userService.getByEmail(principal.getEmail()).orElseThrow();
        double balance = getSellerBalance(seller);

        model.addAttribute("balance", balance);

        return "product/balance/sellerbalance";
    }


    private double getSellerBalance(User seller) {

        List<Payment> payments = paymentService.getProcessedPayments();

        return payments.stream()
                .map(Payment::getReference)
                .filter(ref -> ref instanceof Cart)
                .map(ref -> (Cart) ref)
                .flatMap(cart -> cart.getCartItems().stream())
                .filter(item -> item.getCartItemProduct().getUser().equals(seller))
                .mapToDouble(item -> item.getProductQuantity() * item.getCartItemProduct().getPrice())
                .sum();
    }
}
