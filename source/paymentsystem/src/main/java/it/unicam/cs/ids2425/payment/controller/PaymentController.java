package it.unicam.cs.ids2425.payment.controller;

import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.PaymentDTO;
import it.unicam.cs.ids2425.payment.service.PaymentServiceInterface;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController("PaymentController")
@RequestMapping("/payments/api")
public class PaymentController implements PaymentControllerInterface {
    private final PaymentServiceInterface paymentService;

    public PaymentController(PaymentServiceInterface paymentService) { this.paymentService = paymentService; }

    @GetMapping
    @Override
    public List<Payment> getAll() { return paymentService.getAll(); }

    @GetMapping("/{paymentId}")
    @Override
    public Optional<Payment> get(@PathVariable Long paymentId) { return paymentService.get(paymentId); }

    @PreAuthorize("hasRole = 'BUYER'")
    @PostMapping
    @Override
    public void save(@RequestBody Payment payment) { paymentService.save(payment); }

    @DeleteMapping(path = "{paymentId}")
    @Override
    public void delete(@PathVariable("paymentId") Long paymentId) { paymentService.delete(paymentId); }

    @PutMapping(path = "{paymentId}")
    @Override
    public void update(
            @PathVariable("paymentId") Long paymentId,
            @ModelAttribute PaymentDTO dto) {
        paymentService.update(paymentId, dto);
    }

    @GetMapping("/reference/{referenceId}")
    @Override
    public List<Payment> getByReferenceId(@PathVariable("referenceId") Long referenceId) {
        return paymentService.getByReferenceId(referenceId);
    }

}
