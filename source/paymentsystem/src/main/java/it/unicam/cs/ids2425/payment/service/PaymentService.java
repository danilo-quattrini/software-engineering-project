package it.unicam.cs.ids2425.payment.service;

import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.PaymentDTO;
import it.unicam.cs.ids2425.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService implements PaymentServiceInterface {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) { this.paymentRepository = paymentRepository; }

    @Override
    public List<Payment> getAll() { return paymentRepository.findAll(); }

    @Override
    public Optional<Payment> get(Long id) { return paymentRepository.findById(id); }

    @Override
    public void save(Payment payment) {
        payment.setProcessed(false);
        paymentRepository.save(payment);
    }

    @Override
    public void delete(Long id) { paymentRepository.deleteById(id); }

    @Transactional
    @Override
    public void update(Long id, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new IllegalStateException("Payment with id " + id + " does not exist"));
        updateField(dto.getReference(), payment.getReference(), payment::setReference);
        updateField(dto.isProcessed(), payment.isProcessed(), payment::setProcessed);
    }

    @Override
    public List<Payment> getByReferenceId(Long referenceId) {
        return paymentRepository.findByReferenceId(referenceId);
    }

    @Override
    public List<Payment> getPendingPayments() {
        return paymentRepository.findByProcessedFalse();
    }

    @Override
    public void markPaymentProcessed(Long id) {
        Payment p = paymentRepository.findById(id).orElseThrow();
        p.setProcessed(true);
        paymentRepository.save(p);
    }

    @Override
    public List<Payment> getProcessedPayments() {
        return paymentRepository.findByProcessedTrue();
    }
}
