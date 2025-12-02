package it.unicam.cs.ids2425.payment.service;

import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.PaymentDTO;
import service.ServiceInterface;

import java.util.List;
import java.util.Optional;

public interface PaymentServiceInterface extends ServiceInterface<Payment, PaymentDTO> {
    List<Payment> getByReferenceId(Long referenceId);
    List<Payment> getPendingPayments();
    void markPaymentProcessed(Long id);
    List<Payment> getProcessedPayments();
}
