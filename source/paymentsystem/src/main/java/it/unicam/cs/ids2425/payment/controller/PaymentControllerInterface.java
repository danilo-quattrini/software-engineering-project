package it.unicam.cs.ids2425.payment.controller;

import controller.ControllerInterface;
import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.PaymentDTO;

import java.util.List;
import java.util.Optional;

public interface PaymentControllerInterface extends ControllerInterface<Payment, PaymentDTO> {
    List<Payment> getByReferenceId(Long referenceId);
}
