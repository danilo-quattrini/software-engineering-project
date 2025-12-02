package it.unicam.cs.ids2425.paymentservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/psp")
public class PSPPagesController {
    @GetMapping("/pspnotfound")
    public String pspNotFound() {
        return "Payment Service Provider not found.";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "An error occurred while processing your request.";
    }

    @GetMapping("/order/confirmed")
    public String orderConfirmed() {
        return "Your order has been confirmed.";
    }
}
